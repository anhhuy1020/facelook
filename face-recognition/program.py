import os
import pickle
import threading
import time

import numpy
from PyByteBuffer import ByteBuffer
from imutils.video import VideoStream

import constant
from net_work import network, request, response
import encode_faces
import recognize_faces
import cv2

pi = None
admin = None
state = constant.STATE_LOCKED
delay = 0
name_train = ""
current_milli_time = lambda: int(round(time.time() * 1000))
# load the known faces and embeddings
print("[INFO] loading encodings + face detector...")

if os.path.exists("encodings.pickle") and os.path.isfile("encodings.pickle"):
    data = pickle.loads(open("encodings.pickle", "rb").read())
else:
    data = {}

if os.path.exists("histories.pickle") and os.path.isfile("histories.pickle"):
    histories = pickle.loads(open("histories.pickle", "rb").read())
else:
    histories = []
# initialize the video stream and pointer to output video file, then
# allow the camera sensor to warm up
print("[INFO] starting video stream...")
vs = VideoStream(src=0).start()

startTime = currentTime = None

count = 0
listEncodings = []


def handle_client_request(client: network.client_handler, package: network.req_package):
    global admin
    admin = client
    cmd = package.get_cmd_id()
    print("handle_client_request: {}".format(cmd))
    if cmd == network.CMD.UNLOCK:
        unlock("forced by admin")
    elif cmd == network.CMD.LOCK:
        lock()
    elif cmd == network.CMD.TRAIN:
        req = request.req_train(package)
        global data
        if req.get_name() is not None and not req.get_name() in data:
            global name_train
            name_train = req.get_name()
            start_training()
        else:
            client.send_common(network.CMD.TRAIN, network.ERROR.INVALID_NAME)
    elif cmd == network.CMD.HISTORY:
        res = response.res_history(histories)
        client.send_response(res)


async def handle_client(reader, writer):
    print("get a new client!")
    login_data = ((await reader.read(1024)).decode('utf8'))
    login_data = str(login_data).split(constant.SEPARATOR)
    print("Client login: username = {}, password = {}".format(login_data[0], login_data[1]))
    if login_data == constant.ACCOUNT["pi"]:
        global pi
        pi = network.pi_handler(reader, writer)
        pi.lock()
    elif login_data == constant.ACCOUNT["admin"]:
        bf = ByteBuffer.allocate(network.MAX_PACKAGE_BUFFER_SIZE)
        bf.put(0, 1)  # ping
        bf.put(network.LENGTH_DATA.CMD + network.LENGTH_DATA.ERROR, network.LENGTH_DATA.LENGTH_PACKAGE)
        bf.put(network.CMD.LOGIN, network.LENGTH_DATA.CMD)
        bf.put(network.ERROR.SUCCESS, network.LENGTH_DATA.ERROR)
        length = bf.position
        bf.rewind()
        result = bf.array(length)
        writer.write(result)
        app = network.client_handler(reader, writer, handle_client_request)
        await app.read()
        network.handlers.append(app)
    else:
        bf = ByteBuffer.allocate(network.MAX_PACKAGE_BUFFER_SIZE)
        bf.put(0, 1)    # ping
        bf.put(network.LENGTH_DATA.CMD + network.LENGTH_DATA.ERROR, network.LENGTH_DATA.LENGTH_PACKAGE)
        bf.put(network.CMD.LOGIN, network.LENGTH_DATA.CMD)
        bf.put(network.ERROR.FAIL, network.LENGTH_DATA.ERROR)
        length = bf.position
        bf.rewind()
        result = bf.array(length)
        writer.write(result)


def set_state(s):
    global state
    state = s
    set_delay(constant.DELAY[s])


def set_delay(s=0):
    print("delay = {}".format(s))
    global delay
    delay = s


def handle_locked_state():
    # grab the frame from the threaded video stream
    frame = vs.read()
    check, name_unlock = recognize_faces.recognize_face(frame, data, "hoc")
    if check:
        print("{} has unlocked ".format(name_unlock))
        unlock(name_unlock)


def handle_unlocked_state():
    global delay
    print(delay)
    time.sleep(1)
    delay -= 1
    if delay < 0:
        lock()


def start_training():
    set_state(constant.STATE_TRAINING)
    global currentTime, startTime, count
    currentTime = startTime = time.time()
    count = 0


def on_train_failed():
    print("train failed")
    if admin is not None:
        admin.send_common(network.CMD.TRAIN, network.ERROR.FAIL)
    else:
        print("can't send to client because of no connection!")
    lock()


def on_train_success():
    print("train failed")
    if admin is not None:
        admin.send_common(network.CMD.TRAIN)
    else:
        print("can't send to client because of no connection!")
    lock()


def lock():
    set_state(constant.STATE_LOCKED)
    if pi:
        pi.lock()
    else:
        print("can't lock because of no connection to pi!")


def unlock(name):
    set_state(constant.STATE_UNLOCKED)
    if pi:
        pi.unlock()
        time_unlock = current_milli_time()
        history = {"time": time_unlock, "by": name}
        histories.append(history)
        f = open("histories.pickle", "wb")
        f.write(pickle.dumps(histories))
        f.close()
    else:
        print("can't unlock because of no connection to pi!")


def handle_training_state():
    frame = vs.read()

    global currentTime, startTime, count
    currentTime = time.time()

    check, encoding = encode_faces.train(frame, "hoc",)

    if check:
        print("time train: {}".format(count))
        count += 1
        listEncodings.append(encoding)
        if count >= constant.TIMES_TRAIN:
            data[name_train] = listEncodings
            f = open("encodings.pickle", "wb")
            f.write(pickle.dumps(data))
            f.close()
            on_train_success()

    if currentTime - startTime > constant.TIMES_TRAIN:
        on_train_failed()


def loop():
    while True:
        if state == constant.STATE_LOCKED:
            handle_locked_state()
        elif state == constant.STATE_UNLOCKED:
            handle_unlocked_state()
        elif state == constant.STATE_TRAINING:
            handle_training_state()
            pass
        else:
            pass
        time.sleep(0.2)


try:

    network_thread = threading.Thread(name="network_thread", target=network.run_server,
                                      args=(constant.HOST, constant.PORT, handle_client))
    work_thread = threading.Thread(name="work_thread", target=loop)
    network_thread.start()
    work_thread.start()
    work_thread.join()
    network_thread.join()
    # network.run_server(handle_client=handle_client)
    # loop()
except KeyboardInterrupt:
    print("stop program!")
    cv2.destroyAllWindows()
    vs.stop()

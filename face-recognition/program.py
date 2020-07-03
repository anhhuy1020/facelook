import asyncio
import pickle

from imutils.video import VideoStream

import constant
import network
import encode_faces
import recognize_faces
import cv2

pi = None
state = constant.STATE_LOCKED
delay = 0
# load the known faces and embeddings
print("[INFO] loading encodings + face detector...")
data = pickle.loads(open("encodings.pickle", "rb").read())
# initialize the video stream and pointer to output video file, then
# allow the camera sensor to warm up
print("[INFO] starting video stream...")
vs = VideoStream(src=0).start()


def handle_client_request(msg):
    cmd = msg[0]
    print("Receive a request from client: {}".format(cmd))
    if cmd == "unlock":
        set_state(constant.STATE_UNLOCKED)
        if pi is not None:
            pi.unlock()
        else:
            print("can't unlock because pi is null")
    elif cmd == "lock":
        set_state(constant.STATE_LOCKED)
        if pi is not None:
            pi.lock()


async def handle_client(reader, writer):
    response = ((await reader.read(constant.BUFF_SIZE)).decode('utf8'))
    login_data = str(response).split(constant.SEPARATOR)
    print("Client login: username = {}, password = {}".format(login_data[0], login_data[1]))
    if login_data == constant.ACCOUNT["pi"]:
        global pi
        pi = network.pi_handler(reader, writer)
        pi.lock()
    if login_data == constant.ACCOUNT["admin"]:
        app = network.app_handler(reader, writer, handle_client_request)
        await app.run()
        network.handlers.append(app)


def set_state(s):
    global state
    state = s
    set_delay(constant.DELAY[s])


def set_delay(s=0):
    print("delay = {}".format(s))
    global delay
    delay = s


def handle_clocked_state():
    # grab the frame from the threaded video stream
    frame = vs.read()
    check, name = recognize_faces.recognize_face(frame, data)
    if check:
        print("{} has unlocked ".format(name))
        set_state(constant.STATE_UNLOCKED)
        if pi is not None:
            pi.unlock()


def handle_unlocked_state():
    global delay
    print(delay)
    delay -= 1
    if delay < 0:
        set_state(constant.STATE_LOCKED)
        if pi is not None:
            pi.lock()


async def loop():
    print("loop".format(state))
    if state == constant.STATE_LOCKED:
        handle_clocked_state()
    elif state == constant.STATE_UNLOCKED:
        handle_unlocked_state()
    elif state == constant.STATE_TRAINING:
        pass
    else:
        pass


try:
    event_loop = asyncio.get_event_loop()
    event_loop.create_task(asyncio.start_server(handle_client, constant.HOST, constant.PORT))
    event_loop.create_task(loop)
    event_loop.run_forever()

except KeyboardInterrupt:
    print("stop program!")
    cv2.destroyAllWindows()
    vs.stop()

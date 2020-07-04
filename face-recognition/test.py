import time
import socket

from PyByteBuffer import ByteBuffer

import constant


client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client.connect((constant.HOST, constant.PORT))
print("connect success")
try:
    client.send('admin|~|admin'.encode('utf8'))
    response = None
    time.sleep(2)
    # client.send("unlock".encode('utf8'))
    # time.sleep(6)
    data = ByteBuffer.allocate(20)
    msg = "anhhuy1020"
    length = 2 + 2 + len(msg)
    data.put(length, 2)
    data.put(constant.TRAIN, 2)
    data.put(len(msg), 2)
    data.put(msg)
    length_rs = data.position
    data.rewind()
    result = data.array(length_rs)
    l = len(result)
    client.send(result)
    while response != 'stop':
        response = client.recv(1024).decode("utf8")
        print(response)

except KeyboardInterrupt:
    pass
import socket
import struct
from PIL import Image
import cv2

serverAddressPort = ("127.0.0.1", 5005)


server = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
data = b""
payload_size = struct.calcsize(">L")
server.bind(serverAddressPort)
server.listen(10)
conn, addr = server.accept()

while True:
    size = server.recv(4096)
    data = server.recv(size)
    frame = image = Image.frombytes('RGBA', (500, 700), data, 'raw')
    frame = cv2.imdecode(frame, cv2.IMREAD_COLOR)
    cv2.imshow('ImageWindow', frame)
    cv2.waitKey(1)
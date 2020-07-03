import time
import socket

import constant

PORT = 5005
HOST = '192.168.1.9'

client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client.connect((HOST, PORT))
try:
    client.send('admin|~|admin'.encode('utf8'))
    response = None
    time.sleep(3)
    client.send("unlock".encode('utf8'))
    while response != 'stop':
        response = client.recv(1024).decode("utf8")
        print(response)

except KeyboardInterrupt:
    client.close()

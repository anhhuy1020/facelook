import RPi.GPIO as GPIO
import time
import socket

PORT = 5005
HOST = '192.168.1.9'

servoPIN = 2
GPIO.setmode(GPIO.BCM)
GPIO.setup(servoPIN, GPIO.OUT)

DC_UNLOCK = 2.5
DC_LOCK = 7.5

p = GPIO.PWM(servoPIN, 50)  # GPIO 2 for PWM with 50Hz
p.start(0)  # Initialization

client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client.connect((HOST, PORT))
try:

    client.send('pi|~|123456'.encode('utf8'))
    response = None
    while response != 'stop':
        response = client.recv(1024).decode("utf8")
        print(response)
        if response == 'lock':
            p.ChangeDutyCycle(DC_LOCK)
        if response == 'unlock':
            p.ChangeDutyCycle(DC_UNLOCK)

except KeyboardInterrupt:
    p.stop()
    GPIO.cleanup()

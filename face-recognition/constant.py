# constant for network
PORT = 5005
HOST = '192.168.1.9'
BUFF_SIZE = 1024
SEPARATOR = "|~|"

# constant for lock
STATE_LOCKED = 0
STATE_UNLOCKED = 1
STATE_TRAINING = 2
DELAY = {
    STATE_LOCKED: 0,
    STATE_UNLOCKED: 5,
    STATE_TRAINING: 15
}

ACCOUNT = {
    "pi": [
        "pi",
        "123456"
    ],
    "admin": [
        "admin",
        "admin"
    ]
}


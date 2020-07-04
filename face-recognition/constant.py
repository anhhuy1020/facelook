# constants for network
PORT = 5005
HOST = '192.168.1.3'
SEPARATOR = "|~|"




# constants for lock
STATE_LOCKED = 0
STATE_UNLOCKED = 1
STATE_TRAINING = 2
DELAY = {
    STATE_LOCKED: 0,
    STATE_UNLOCKED: 5,
    STATE_TRAINING: 15
}

# constants for training
TIMEOUT = 20
TIMES_TRAIN = 20

# constants for accounts
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


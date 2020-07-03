import asyncio
import constant


session = 0
handlers = []


def get_session():
    global session
    session += 1
    return session


class pi_handler:
    def __init__(self, reader, writer):
        self.reader = reader
        self.writer = writer

    def lock(self):
        self.writer.write("lock".encode('utf8'))

    def unlock(self):
        self.writer.write("unlock".encode('utf8'))


class app_handler:
    def __init__(self, reader, writer, handle_client_request_cb):
        self.reader = reader
        self.writer = writer
        self.handle_request_cb = handle_client_request_cb
        self.session = get_session()

    def send(self, msg):
        self.writer.write(msg + "\n").encode('utf8')

    async def run(self):
        while True:
            msg = str((await self.reader.read(constant.BUFF_SIZE)).decode('utf8')).split(constant.SEPARATOR)
            if self.handle_request_cb is not None:
                self.handle_request_cb(msg)
            else:
                print("Receive a message but have not handling function: {}".format(msg[0]))


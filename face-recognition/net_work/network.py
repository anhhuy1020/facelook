import asyncio
from enum import Enum

from PyByteBuffer import ByteBuffer

import constant

session = 0
handlers = []

MAX_PACKAGE_BUFFER_SIZE = 5012
CHAR_SET = 'utf8'


class LENGTH_DATA(int):
    CMD = 2     # short
    STR = 2   # short
    ERROR = 2    # short
    LENGTH_PACKAGE = 4  # int


class CMD(int):
    LOCK = 0
    UNLOCK = 1
    TRAIN = 2
    HISTORY = 3
    LOGIN = 99


class ERROR(int):
    SUCCESS = 0
    FAIL = 1
    INVALID_NAME = 2


def get_session():
    global session
    session += 1
    return session


class base_package:
    def __init__(self, cmd_id: int):
        self.cmd_id = cmd_id
        self._data = None

    def get_cmd_id(self) -> int:
        return self.cmd_id

    def get_raw_data(self) -> bytearray:
        return self._data

    def set_raw_data(self, data: bytearray):
        self._data = data


class req_package(base_package):
    pass


class res_package(base_package):
    def __init__(self, cmd_id: int, error: int = ERROR.SUCCESS):
        base_package.__init__(self, cmd_id)
        self.error = error

    def get_error(self) -> int:
        return self.error

    def set_error(self, error: int):
        self.error = error

    def get_length_data(self) -> int:
        if self._data is None:
            return LENGTH_DATA.ERROR + LENGTH_DATA.CMD
        # 2 for cmd_id and 2 for error
        return len(self._data) + LENGTH_DATA.ERROR + LENGTH_DATA.CMD


class base_response:
    def __init__(self, cmd_id: int, error: int = ERROR.SUCCESS):
        self._package = res_package(cmd_id, error)

    def get_package(self) -> res_package:
        return self._package

    def prepare_package(self):
        self._package.set_raw_data(self.create_data())

    # for override
    def create_data(self) -> bytearray:
        pass

    @staticmethod
    def make_buffer() -> ByteBuffer:
        return ByteBuffer.allocate(MAX_PACKAGE_BUFFER_SIZE)

    @staticmethod
    def put_str(bf: ByteBuffer, value: str):
        if value is None:
            value = ""
        bf.put(len(value), LENGTH_DATA.STR)
        bf.put(value)

    @staticmethod
    def pack_buffer(bf: ByteBuffer) -> bytearray:
        length = bf.position
        bf.rewind()
        return bf.array(length)


class base_request:
    def __init__(self, package: req_package):
        self._package = package
        self.unpack_data()

    def get_package(self) -> req_package:
        return self._package

    # for override
    def unpack_data(self):
        pass

    def make_buffer(self) -> ByteBuffer:
        return ByteBuffer.wrap(self.get_package().get_raw_data())

    @staticmethod
    def read_str(bf: ByteBuffer) -> str:
        length = bf.get(LENGTH_DATA.STR)
        return bf.array(length).decode(CHAR_SET)


class pi_handler:
    def __init__(self, reader, writer):
        self.reader = reader
        self.writer = writer

    def lock(self):
        self.writer.write("lock".encode('utf8'))

    def unlock(self):
        self.writer.write("unlock".encode('utf8'))


class client_handler:
    def __init__(self, reader, writer, handle_client_request_cb):
        self.reader = reader
        self.writer = writer
        self.handle_request_cb = handle_client_request_cb
        self.session = get_session()

    def send_common(self, cmd_id: int, error: int = ERROR.SUCCESS):
        package = res_package(cmd_id, error)
        self.send(package)

    def send_response(self, res: base_response):
        res.prepare_package()
        self.send(res.get_package())

    def send(self, package: res_package):
        bf = ByteBuffer.allocate(1 + package.get_length_data() + LENGTH_DATA.LENGTH_PACKAGE)
        bf.put(0, 1)    # for ping
        length = package.get_length_data()
        bf.put(package.get_length_data(), LENGTH_DATA.LENGTH_PACKAGE)
        bf.put(package.get_cmd_id(), LENGTH_DATA.CMD)
        bf.put(package.get_error(), LENGTH_DATA.ERROR)
        length = bf.position
        bf.rewind()
        result = bf.array(length)
        if package.get_raw_data() is not None:
            result.extend(package.get_raw_data())
        self.writer.write(result)

    async def read(self):
        while True:
            ping = int.from_bytes(await self.reader.read(1),
                                  byteorder='big', signed=True)
            print("ping == {}".format(ping))
            if ping == 0:
                length_package = int.from_bytes(await self.reader.read(LENGTH_DATA.LENGTH_PACKAGE),
                                                byteorder='big', signed=True)
                if length_package >= 2:
                    cmd = int.from_bytes(await self.reader.read(LENGTH_DATA.CMD),
                                         byteorder='big', signed=True)
                    package = req_package(cmd)
                    length_package -= 2
                    if length_package > 0:
                        data = await self.reader.read(length_package)
                        package.set_raw_data(data)
                    self.handle_request_cb(self, package)


def run_server(HOST=constant.HOST, PORT=constant.PORT, handle_client=None):
    loop = asyncio.new_event_loop()
    loop.create_task(asyncio.start_server(handle_client, HOST, PORT))
    loop.run_forever()

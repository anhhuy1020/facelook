from PyByteBuffer import ByteBuffer

from net_work.network import base_response , res_package, CMD


class res_history(base_response):
    def __init__(self,histories: list):
        super().__init__(CMD.HISTORY)
        self.histories = histories

    def create_data(self) -> bytearray:
        bf: ByteBuffer = self.make_buffer()
        bf.put(len(self.histories), 4)
        for history in self.histories:
            bf.put(history["time"], 8)  # long
            self.put_str(bf, history["by"])
        return self.pack_buffer(bf)


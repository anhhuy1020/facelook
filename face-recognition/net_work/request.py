from net_work.network import base_request as base, req_package


class req_train(base):
    def __int__(self, package: req_package):
        base.__init__(self, package)
        self.name = None

    def get_name(self):
        return self.name

    def unpack_data(self):
        bf = self.make_buffer()
        self.name = self.read_str(bf)

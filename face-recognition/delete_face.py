import argparse
import os
import pickle

import constant

ap = argparse.ArgumentParser()
ap.add_argument("-n", "--name", required=True)
args = vars(ap.parse_args())

if os.path.exists("encodings.pickle") and os.path.isfile("encodings.pickle"):
    data: dict = pickle.loads(open("encodings.pickle", "rb").read())
    if args["name"] in data:
        data.pop(args["name"])
        f = open("encodings.pickle", "wb")
        f.write(pickle.dumps(data))
        f.close()
        print("delete {}'s face completed".format(args["name"]))
    else:
        print("{}'s face hasn't been added yet".format(args["name"]))
else:
    print("can't find encodings.pickle")

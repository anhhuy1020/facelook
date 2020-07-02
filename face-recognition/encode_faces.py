from imutils.video import VideoStream
import face_recognition
import argparse
import pickle
import cv2
import os
import time
import imutils


def train(name, detectionMethod):
    vs = VideoStream(src=0).start()
    startTime = lastTime = currentTime = time.time()
    interval = 0.2
    duration = 15
    count = 0
    listEncodings = []
    print("Start training")
    while (currentTime - startTime < duration) or (count < 30):

        # grab the frame from the threaded video stream
        currentTime = time.time()
        if (currentTime - lastTime < interval):
            continue
        lastTime = currentTime
        print("Time train: {}".format(count))
        frame = vs.read()
        # convert the input frame from BGR to RGB then resize it to have
        # a width of 750px (to speedup processing)
        rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

        # detect the (x, y)-coordinates of the bounding boxes
        # corresponding to each face in the input image
        boxes = face_recognition.face_locations(rgb,
                                                model=detectionMethod)
        if (len(boxes) <= 0):
            cv2.imshow("Frame", frame)
            key = cv2.waitKey(1) & 0xFF

            # if the `q` key was pressed, break from the loop
            if key == ord("q"):
                break
            continue

        count += 1
        encodings = face_recognition.face_encodings(rgb, boxes)
        listEncodings.append(encodings[0])
        for (top, right, bottom, left) in boxes:
            # draw the predicted face name on the image
            cv2.rectangle(frame, (left, top), (right, bottom),
                          (0, 255, 0), 2)
        cv2.imshow("Frame", frame)
        key = cv2.waitKey(1) & 0xFF

        # if the `q` key was pressed, break from the loop
        if key == ord("q"):
            break

    # compute the facial embedding for the face
    if os.path.exists("encodings.pickle") and os.path.isfile("encodings.pickle"):
        data = pickle.loads(open("encodings.pickle", "rb").read())
    else:
        data = {}
    data[name] = listEncodings
    f = open("encodings.pickle", "wb")
    f.write(pickle.dumps(data))
    f.close()

from imutils.video import VideoStream
import face_recognition
import argparse
import pickle
import cv2
import os
import time
import imutils


def train(frame, detectionMethod):
    # convert the input frame from BGR to RGB
    rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    rgb = imutils.resize(rgb, width=750)
    r = frame.shape[1] / float(rgb.shape[1])
    # detect the (x, y)-coordinates of the bounding boxes
    # corresponding to each face in the input image
    boxes = face_recognition.face_locations(rgb,
                                            model=detectionMethod)
    if (len(boxes) <= 0):
        cv2.imshow("Frame", frame)
        key = cv2.waitKey(1) & 0xFF
        if key == ord("q"):
            return False, None
        return False, None
    encodings = face_recognition.face_encodings(rgb, boxes)
    # rescale the face coordinates
    for (top, right, bottom, left) in boxes:
        top = int(top * r)
        right = int(right * r)
        bottom = int(bottom * r)
        left = int(left * r)
        # draw the predicted face name on the image
        cv2.rectangle(frame, (left, top), (right, bottom),
                      (0, 255, 0), 2)
    cv2.imshow("Frame", frame)
    key = cv2.waitKey(1) & 0xFF

    # if the `q` key was pressed, break from the loop
    if key == ord("q"):
        return True, encodings[0]
    return True, encodings[0]



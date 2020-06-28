from imutils.video import VideoStream
import face_recognition
import imutils
import pickle
import time
import cv2


def recognizing(detectionMethod):
    # load the known faces and embeddings
    print("[INFO] loading encodings...")
    data = pickle.loads(open("encodings.pickle", "rb").read())
    # initialize the video stream and pointer to output video file, then
    # allow the camera sensor to warm up
    print("[INFO] starting video stream...")
    vs = VideoStream(src=0).start()
    writer = None
    time.sleep(2.0)
    start_time = time.time()
    duration = 15

    while True:
        # grab the frame from the threaded video stream
        frame = vs.read()

        # convert the input frame from BGR to RGB then resize it to have
        # a width of 750px (to speedup processing)
        rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        rgb = imutils.resize(frame, width=750)
        r = frame.shape[1] / float(rgb.shape[1])

        # detect the (x, y)-coordinates of the bounding boxes
        # corresponding to each face in the input frame, then compute
        # the facial embeddings for each face
        boxes = face_recognition.face_locations(rgb,
                                                model=detectionMethod)
        if (len(boxes) <= 0):
            cv2.imshow("Frame", frame)
            key = cv2.waitKey(1) & 0xFF
            # if the `q` key was pressed, break from the loop
            if key == ord("q"):
                break
            continue

        for (top, right, bottom, left) in boxes:
            # rescale the face coordinates
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
                break

        encoding = face_recognition.face_encodings(rgb, boxes)[0]
        names = []

        # loop over the facial embeddings
        for name in data:
            known_encodings = data[name]
            matches = face_recognition.compare_faces(known_encodings,
                                                     encoding)
            fail = 0
            threshold = 0 #todo hard code
            # check to see if we have found a match
            for match in matches:
                if match == False:
                    fail += 1

            if (fail <= threshold):
                print("OK {}".format(fail))
                return True
            # update the list of names
            names.append(name)



        current_time = time.time()
        elapsed_time = current_time - start_time
        if elapsed_time > duration:
            print("Finished because of running out of time!")
            return False
    # do a bit of cleanup
    cv2.destroyAllWindows()
    vs.stop()

    # return ret

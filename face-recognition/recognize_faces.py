import face_recognition
import imutils
import cv2


def recognize_face(frame, data, detectionMethod):
    # resize to 500px (to speedup processing)
    frame = imutils.resize(frame, width=500)

    # convert the input frame from BGR to RGB then resize it to have
    # a width of 750px (to speedup processing)
    rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    rgb = imutils.resize(frame, width=750)
    r = frame.shape[1] / float(rgb.shape[1])

    # detect the (x, y)-coordinates of the bounding boxes
    # corresponding to each face in the input frame, then compute
    # the facial embeddings for each face
    boxes = face_recognition.face_locations(rgb, model=detectionMethod)

    # can't detect face's bounding
    if len(boxes) <= 0:
        cv2.imshow("Frame", frame)
        key = cv2.waitKey(1) & 0xFF
        # if the `q` key was pressed, break from the loop
        if key == ord("q"):
            return False, ""
        return False, ""

    for (top, right, bottom, left) in boxes:
        # rescale the face coordinates
        top = int(top * r)
        right = int(right * r)
        bottom = int(bottom * r)
        left = int(left * r)
        # draw the predicted face name on the image
        cv2.rectangle(frame, (left, top), (right, bottom),
                      (0, 255, 0), 2)

        # show frame
        cv2.imshow("Frame", frame)
        key = cv2.waitKey(1) & 0xFF
        # if the `q` key was pressed, break from the loop
        if key == ord("q"):
            break
        continue

    # encode the face in frame
    encoding = face_recognition.face_encodings(rgb, boxes)[0]

    # loop over the facial embeddings
    for name in data:
        known_encodings = data[name]
        matches = face_recognition.compare_faces(known_encodings,
                                                 encoding)
        fail = 0
        threshold = 0

        # find number of encoding face not match
        for match in matches:
            if not match:
                fail += 1

        if fail <= threshold:
            return True, name

        print("Fail {}".format(fail))
    return False, ""

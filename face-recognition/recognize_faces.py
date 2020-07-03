import face_recognition
import imutils
import cv2


def recognize_face(frame, data):
    detector = cv2.CascadeClassifier("haarcascade_frontalface_default.xml")
    # resize to 500px (to speedup processing)
    frame = imutils.resize(frame, width=500)

    # convert the input frame from (1) BGR to grayscale (for face
    # detection) and (2) from BGR to RGB (for face recognition)
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

    # detect faces in the grayscale frame
    rects = detector.detectMultiScale(gray, scaleFactor=1.1,
                                      minNeighbors=5, minSize=(30, 30),
                                      flags=cv2.CASCADE_SCALE_IMAGE)

    # OpenCV returns bounding box coordinates in (x, y, w, h) order
    # but we need them in (top, right, bottom, left) order, so we
    # need to do a bit of reordering
    boxes = [(y, x + w, y + h, x) for (x, y, w, h) in rects]

    if len(boxes) <= 0:
        cv2.imshow("Frame", frame)
        cv2.waitKey()
        return False

    for (top, right, bottom, left) in boxes:

        # draw the predicted face name on the image
        cv2.rectangle(frame, (left, top), (right, bottom),
                      (0, 255, 0), 2)

        cv2.imshow("Frame", frame)
        cv2.waitKey()

    encoding = face_recognition.face_encodings(rgb, boxes)[0]

    # loop over the facial embeddings
    for name in data:
        known_encodings = data[name]
        matches = face_recognition.compare_faces(known_encodings,
                                                 encoding)
        fail = 0
        threshold = 0
        # check to see if we have found a match
        for match in matches:
            if not match:
                fail += 1
        if fail <= threshold:
            return True, name
    return False

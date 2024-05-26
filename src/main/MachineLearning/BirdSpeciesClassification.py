from os.path import splitext
import json
import requests
from cv2 import imread, cvtColor, COLOR_BGR2RGB, COLOR_RGB2BGR, CascadeClassifier, medianBlur, imwrite

headers = {
    "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZDE4N2U4NzUtOWQ0NS00Yzk2LTgwYTMtZDcxYmYzYjQyMjYyIiwidHlwZSI6ImFwaV90b2tlbiJ9.07PesQmLAQ8lJSR8CaUrC1reCASfS1YT2kWbTL8nHdA"
}

url = "https://api.edenai.run/v2/image/object_detection"
data = {
    "show_original_response": False,
    "fallback_providers": "",
    "providers": "amazon"
}

image_extensions = ['.jpg', '.jpeg', '.png']

bird_species = ["Blackbird","Crow","Finch","Dove","Pigeon","Sparrow","Magpie","Robin","Wren"]

def isImage(filepath):
    file_extension = splitext(filepath)[1].lower()
    return file_extension in image_extensions

from cv2 import imread, resize, imsave, INTER_LINEAR
def resizeImage(filepath, size=1000):
    original = imread(filepath)
    resized = resize(original, (size, size), interpolation=INTER_LINEAR)
    imsave(filepath, resized)

def detect_faces(image):
    cascade_files = [ 
        './haarcascade_frontalface_default.xml',
        './haarcascade_frontalface_alt.xml',
        './haarcascade_profileface.xml'
    ]

    for cascade_file in cascade_files:
        cascade = CascadeClassifier(cascade_file)
        face_data = cascade.detectMultiScale(image, scaleFactor=2, minNeighbors=4)
        if len(face_data) > 0:
            return face_data
    return []

def blur_faces(image, face_data):
    for x, y, w, h in face_data:
        blur_radius = min(w, h) // 10
        if blur_radius % 2 == 0:
            blur_radius += 1
        image[y:y+h, x:x+w] = medianBlur(image[y:y+h, x:x+w], blur_radius)
    return image

# from os import path
def isBird(filepath, confidence=0.95):
    if not isImage(filepath):
        raise ValueError("The provided argument does not lead to an image file!")

    # commented out this feature because we corrupted some files
    # # input image resizing
    # if path.getsize(filepath) > 125000:
    #     resizeImage(filepath)

    with open(filepath, 'rb') as image_file:
        files = {'file': image_file}
        response = requests.post(url, data=data, files=files, headers=headers)
        result = json.loads(response.text)

    if len(result.get('amazon', {}).get('items', [])) > 0:
        labels_and_confidence = {item['label']: item['confidence'] for item in result['amazon']['items']}

        # commented out this locally working feature because downloading opencv-python on the Pi took too long
        # face blurring for privacy
        # if ('Face' not in labels_and_confidence or labels_and_confidence['Face'] < 0.75) and ('Head' not in labels_and_confidence or labels_and_confidence['Head'] < 0.75):
        #     image = imread(filepath)
        #     image = cvtColor(image, COLOR_BGR2RGB)
        #     face_data = detect_faces(image)
        #     if len(face_data) > 0:
        #         image = blur_faces(image, face_data)
        #         image = cvtColor(image, COLOR_RGB2BGR)
        #         imwrite(filepath,image)

        # bird species classification
        if 'Bird' in labels_and_confidence and labels_and_confidence['Bird'] >= confidence:
            for label,conf in labels_and_confidence.items():
                if label in bird_species:
                    return True, label, "%.2f" % (conf * 100)
            return True, None, None
    
    return False, None, None
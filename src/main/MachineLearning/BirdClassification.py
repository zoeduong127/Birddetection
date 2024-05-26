from os.path import splitext
import json
import requests

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

def isImage(filepath):
    file_extension = splitext(filepath)[1].lower()
    return file_extension in image_extensions

from cv2 import imread, imwrite, resize, INTER_LINEAR
def resizeImage(filepath, size=1000):
    original = imread(filepath)
    resized = resize(original, (size, size), interpolation=INTER_LINEAR)
    imwrite(filepath, resized)

from os import path
def isBird(filepath, confidence=0.95):
    if not isImage(filepath):
        raise ValueError("The provided argument does not lead to an image file!")
    
    Bird = False
    
    # used for input size test
    if path.getsize(filepath) > 125000:
        resizeImage(filepath)

    with open(filepath, 'rb') as image_file:
        files = {'file': image_file}
        response = requests.post(url, data=data, files=files, headers=headers)
        result = json.loads(response.text)

    if len(result.get('amazon', {}).get('items', [])) > 0:
        for item in result['amazon']['items']:
            if item.get('label') == "Bird" and item.get('confidence', 0) >= confidence:
                Bird = True
                break

    return Bird
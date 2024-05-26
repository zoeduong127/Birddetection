import requests
import datetime
import json

ip = '145.126.60.24'
port = '8080'

auth = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYXNwaSIsImFjY291bnRfaWQiOjcyLCJleHAiOjE2OTk0NTYyNDJ9.YVjfzVs4Y9QyVdqUXTw-D25CpIlCtYNemHs7ALz-xP8'

timestamp = str(datetime.datetime.now().replace(microsecond=0))
print(timestamp)
# timestamp = datetime.datetime.fromtimestamp(timestamp)
# timestamp = datetime.datetime.strftime('%Y-%m-%d %H:%M:%S')

visit_id = 69
filepath = 'data/HouseSparrow/2.jpg'
bird,species,confidence = isBird(filepath)

base_url = f'http://{ip}:{port}/bad/api'

if not bird or not species:
    # add visit with images
    r = requests.put(base_url + '/images/archive/visits',
                     json = {
                        'visitId':visit_id,
                        "species":"No Bird",
                        "arrival":timestamp,
                        "departure":timestamp,
                        "visitLen":310,
                        "accuracy":95.12,
                        "images":[ 
                            {
                                "imageId": 1,
                                "visitId": 1,
                                "date": timestamp,
                                "image_path": "meta/img/main/bird1.jpeg"
                            }
                        ]
                     },
                     headers = {
                        'Authorization':auth,
                        'Content-Type':'application/json'
                        }
                    )

else:
    # add visit with images
    r = requests.put(base_url + '/images/main/visits',
                     json = {
                        'visitId':visit_id,
                        "species":species,
                        "arrival":timestamp,
                        "departure":timestamp,
                        "visitLen":310,
                        "accuracy":confidence,
                        "images":[ 
                            {
                                "imageId": 1,
                                "visitId": 1,
                                "date": timestamp,
                                "image_path": "meta/img/main/bird1.jpeg"                            }
                        ]
                     },
                     headers = {
                        'Authorization':auth,
                        'Content-Type':'application/json'
                        }
                    )

if r != 200:
    # add filepath to backlog
    filepath
    print(r)
    print(r.status_code)

else:
    print("Yay!")
import requests
import json
def send_api():
    api = 'http://localhost:8087/ai/add-alert'

    data = {
        "petName": "pet2",
        "userName": "user1",
        "alertMessage": "cảnh báo nguy hiểm, có lửa",
        "location": "trên cửa"
    }

    response = requests.post(api, json=data)

    if response.status_code == 201:
        print("Response: ", response.text)
    else:
        print("Error: ", response.status_code)

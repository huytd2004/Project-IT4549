import requests
import json
def send_api():
    api = 'http://localhost:4000/notification'

    data =     {
        "petName": "Bốt ngoo",
        "userName": "Nguyễn Quốc Khánh",
        "alertMessage": "cảnh báo nguy hiểm, có lửa",
        "location": "trên cửa"
    }

    response = requests.post(api, json=data)

    if response.status_code == 201:
        print("Response: ", response.text)
    else:
        print("Error: ", response.status_code)

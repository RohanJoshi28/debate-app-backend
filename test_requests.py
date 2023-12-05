import requests

requests.post("http://127.0.0.1:5000/save_email", data={"name": "Josh", "email": "abc123@gmail.com"})
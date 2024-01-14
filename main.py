import os

import requests
from flask import Flask, jsonify, request, Response
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
from dotenv import load_dotenv

from flask_jwt_extended import create_access_token
from flask_jwt_extended import get_jwt
from flask_jwt_extended import get_jwt_identity
from flask_jwt_extended import jwt_required
from flask_jwt_extended import JWTManager
from flask_jwt_extended import set_access_cookies
from flask_jwt_extended import unset_jwt_cookies

from datetime import datetime
from datetime import timedelta
from datetime import timezone
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///database.db'
db = SQLAlchemy(app)

app.app_context().push()
CORS(app, origins=['http://localhost:3000'], supports_credentials=True)


load_dotenv()

app.config["JWT_COOKIE_SECURE"] = True
app.config['JWT_SECRET_KEY'] = os.environ['JWT_SECRET_KEY']
app.config['JWT_TOKEN_LOCATION'] = ['cookies']
app.config["JWT_ACCESS_TOKEN_EXPIRES"] = timedelta(hours=1)
jwt = JWTManager(app)

GOOGLE_CLIENT_ID = os.environ['GOOGLE_CLIENT_ID']
GOOGLE_CLIENT_SECRET = os.environ['GOOGLE_CLIENT_SECRET']



#Define the RequestCount model
class RequestCount(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    count = db.Column(db.Integer, default=0)

class User(db.Model):
    id = db.Column(db.Integer, primary_key = True)
    email = db.Column(db.String)
    name = db.Column(db.String)

db.create_all()

if RequestCount.query.get(1) is None:
    new_counter = RequestCount(id=1, count=0)
    db.session.add(new_counter)
    db.session.commit()

# @app.before_request
# def handle_preflight():
#     if request.method == "OPTIONS":
#         res = Response()
#         res.headers['Access-Control-Allow-Credentials'] = 'true'
#         return res
    
@app.route('/', methods=['GET'])
def hello_world():
    return "hello world"

@app.route('/login', methods=['POST'])
def login():
    auth_code = request.get_json()['code']

    data = {
        'code': auth_code,
        'client_id': GOOGLE_CLIENT_ID,
        'client_secret': GOOGLE_CLIENT_SECRET, 
        'redirect_uri': 'postmessage',
        'grant_type': 'authorization_code'
    }

    response = requests.post('https://oauth2.googleapis.com/token', data=data).json()
    headers = {
        'Authorization': f'Bearer {response["access_token"]}'
    }
    user_info = requests.get('https://www.googleapis.com/oauth2/v3/userinfo', headers=headers).json()

    #check if user is in database, if not add the user 
    #FYI FOR LATER ON: MAYBE ONLY ALLOW LOGIN IF USER IS AN EXISTING USER; BC ONLY ADMINS CAN ACCESS
    # user = User.query.filter_by(email=user_info['email']).first()
    # if user == None:
    #     user = User(user_info['name'], user_info['email'])
    #     db.session.add(user)
    #     db.session.commit()
    # else:
    #     user.name = user_info['name']
    #     db.session.commit()

    jwt_token = create_access_token(identity=user_info['email'])  
    response = jsonify(user=user_info)
    response.set_cookie('access_token_cookie', value=jwt_token, secure=True)
    return response, 200

@app.after_request
def refresh_expiring_jwts(response):
    try:
        exp_timestamp = get_jwt()["exp"]
        now = datetime.now(timezone.utc)
        target_timestamp = datetime.timestamp(now + timedelta(minutes=30))
        if target_timestamp > exp_timestamp:
            access_token = create_access_token(identity=get_jwt_identity())
            set_access_cookies(response, access_token)
        return response
    except (RuntimeError, KeyError):
        # Case where there is not a valid JWT. Just return the original response
        return response
        
@app.route("/logout", methods=["POST"])
def logout():
    response = jsonify({"msg": "logout successful"})
    unset_jwt_cookies(response)
    return response

@app.route("/protected", methods=["GET"])
@jwt_required()
def protected():
    jwt_token = request.cookies.get('access_token_cookie')
    current_user = get_jwt_identity()
    response = jsonify(logged_in_as=current_user)
    return response, 200


#Route to increment request count
@app.route('/increment', methods=['POST'])
def increment():
    counter = RequestCount.query.get(1)
    if counter:
        counter.count += 1
        db.session.commit()
    return jsonify({"message": "Request count incremented", "currentCount": counter.count}), 200

#Route to increment request count
@app.route('/save_email', methods=['POST'])
def save_email():
    name = request.get_json()['name']
    email = request.get_json()['email']

    user = User.query.filter_by(email=email).first()
    if user == None:
        user = User(name=name, email=email)
        db.session.add(user)
        db.session.commit()
    else:
        user.name = name
        db.session.commit()
    return "Success", 200

if __name__ == '__main__':
    app.run(debug=True)
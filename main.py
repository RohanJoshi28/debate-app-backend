import os
import subprocess

import requests
from flask import Flask, jsonify, request, Response, render_template, send_from_directory, send_file
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
from werkzeug.utils import secure_filename
from dotenv import load_dotenv
#
from flask_jwt_extended import create_access_token, decode_token, get_unverified_jwt_headers, verify_jwt_in_request
from flask_jwt_extended import get_jwt
from flask_jwt_extended import get_jwt_identity
from flask_jwt_extended import jwt_required
from flask_jwt_extended import JWTManager
from flask_jwt_extended import set_access_cookies
from flask_jwt_extended import unset_jwt_cookies

from datetime import datetime
from datetime import timedelta
from datetime import timezone
from flask_migrate import Migrate

# import bleach

from sendgrid import SendGridAPIClient
from sendgrid.helpers.mail import Mail

# from wtforms import StringField, IntegerField, SubmitField, validators, DateField, SelectField
# from wtforms.validators import DataRequired

import subprocess
from subprocess import Popen, PIPE, run, CalledProcessError
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///database.db'
db = SQLAlchemy(app)

app.app_context().push()
#CORS(app, origins=['http://localhost:3000'], supports_credentials=True) #<--disable on deploy
# # CORS(app, origins=['http://localhost:3000', 'https://test-debate-frontend-update-deploy.onrender.com', 'https://debate-app-backend.onrender.com'], supports_credentials=True)
CORS(app, resources={r"/*": {"origins": "https://www.rohanjoshi.dev", "supports_credentials": True}}) #<--enable on deploy

migrate = Migrate(app, db)

load_dotenv()


UPLOAD_FOLDER = './tmp/maps' 
ALLOWED_EXTENSIONS = {'pdf', 'png', 'jpeg', 'jpg'}
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config["JWT_COOKIE_SECURE"] = True
app.config['JWT_SECRET_KEY'] = os.environ['JWT_SECRET_KEY']
app.config['JWT_TOKEN_LOCATION'] = ['cookies']
app.config["JWT_ACCESS_TOKEN_EXPIRES"] = timedelta(hours=1)
app.config["JWT_COOKIE_SAMESITE"] = "None"
app.config["JWT_COOKIE_DOMAIN"] = "rohanjoshi.dev"
jwt = JWTManager(app)

GOOGLE_CLIENT_ID = os.environ['GOOGLE_CLIENT_ID']
GOOGLE_CLIENT_SECRET = os.environ['GOOGLE_CLIENT_SECRET']


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS
#Define the RequestCount model
class RequestCount(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    count = db.Column(db.Integer, default=0)

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String)
    name = db.Column(db.String)

class Admin(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String)
    name = db.Column(db.String)

class Debater(db.Model):
    __tablename__ = "debater"
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String, unique=True)
    name = db.Column(db.String)
    partnership_id = db.Column(db.Integer, db.ForeignKey('partnership.id'), nullable=False)

class Partnership(db.Model):
    __tablename__ = "partnership"
    id = db.Column(db.Integer, primary_key=True)
    debaters = db.relationship("Debater", backref="partnership")
    school_id = db.Column(db.Integer, db.ForeignKey('school.id'), nullable=False)

class School(db.Model):
    __tablename__ = "school"
    id = db.Column(db.Integer, primary_key=True)
    parternships = db.relationship("Partnership", backref="school")
    name = db.Column(db.String)
    num_debaters = db.Column(db.Integer)
    num_judges = db.Column(db.Integer)
    coach = db.relationship("Coach", uselist=False, backref="school")
    tournaments_hosting = db.relationship("Tournament", foreign_keys="Tournament.host_school_id", backref="host_school")
    tournaments = db.relationship("Tournament", secondary="tournament_school", back_populates="schools")

class Coach(db.Model):
    __tablename__ = "coach"
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String, unique=True)
    name = db.Column(db.String)
    school_id = db.Column(db.Integer, db.ForeignKey('school.id'), nullable=False)

class Tournament(db.Model):
    __tablename__ = "tournament"
    id = db.Column(db.Integer, primary_key=True)
    host_school_id = db.Column(db.Integer, db.ForeignKey('school.id'), nullable=False)
    schools = db.relationship("School", secondary="tournament_school", back_populates="tournaments")
    datetime = db.Column(db.DateTime, default=datetime.utcnow)

tournament_school = db.Table(
    "tournament_school",
    db.Column("tournament_id", db.Integer, db.ForeignKey("tournament.id")),
    db.Column("school_id", db.Integer, db.ForeignKey("school.id")),
)
class RoomAssignment(db.Model):
    __tablename__ = "room_assignment"
    id = db.Column(db.Integer, primary_key=True)
    tournament_id = db.Column(db.Integer, db.ForeignKey("tournament.id"), nullable=False)
    match_index = db.Column(db.Integer, nullable=False)
    room_number = db.Column(db.String, nullable=False)

class Match(db.Model):
    __tablename__ = 'match'
    id = db.Column(db.Integer, primary_key=True)
    tournament_id = db.Column(db.Integer, db.ForeignKey('tournament.id'), nullable=False)
    round_number = db.Column(db.Integer, nullable=False)
    affirmative = db.Column(db.String, nullable=False)
    negative = db.Column(db.String, nullable=False)
    judge = db.Column(db.String, nullable=False)

db.create_all()

#db model creation tests

# if RequestCount.query.get(1) is None:
#     new_counter = RequestCount(id=1, count=0)
#     db.session.add(new_counter)
#     db.session.commit()

# db.session.query(School).delete()
# db.session.query(Tournament).delete()

# school1 = School(id=1, name="Bergen Academies", num_debaters=2, num_judges=2)
# school2 = School(id=2, name="Mountain Valley", num_debaters=2, num_judges=2)
# school3 = School(id=3, name="Bridgewater High", num_debaters=3, num_judges=1)

# db.session.add(school1)
# db.session.add(school2)
# db.session.add(school3)
# db.session.commit()

# tournament = Tournament(id=1, host_school_id=school1.id, datetime=datetime(2024, 2, 5, 17, 0))
# db.session.add(tournament)
# db.session.commit()

# tournament.schools.append(school1)
# tournament.schools.append(school2)
# tournament.schools.append(school3)
# db.session.commit()

@app.route('/upload', methods=['POST'])
def upload_file():
    print("BEGIN LOGS")
    print(os.listdir())
    print()
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    
    if not admin:
        return "Unauthorized", 401
    
    if 'file' not in request.files:
        return 'No file part', 400

    file = request.files['file']

    if file.filename == '':
        return 'No selected file', 400

    if not allowed_file(file.filename):
        return 'File type not allowed', 400
   

    if 'school_name' not in request.form:
        return 'No school name provided', 400
    
    print("GOT PAST IF STATEMENTS")
    
    school_name = request.form['school_name']
    
    # Replace spaces with underscores in the school name
    sanitized_school_name = school_name.replace(' ', '_')
    
    # Use secure_filename to sanitize the original filename
    filename = secure_filename(file.filename)
    
    # Generate the new filename with sanitized school name and original file extension
    _, file_extension = os.path.splitext(filename)
    new_filename = f"{sanitized_school_name}{file_extension}"
    
    # Save the file to the upload directory
    file_path = os.path.join(app.config['UPLOAD_FOLDER'], new_filename)
    #logs

    file.save(file_path)
    print("FILE PATH")
    print(file_path)
    print(os.listdir(app.config['UPLOAD_FOLDER']))

    return 'File uploaded successfully', 200

@app.route('/schoolmap/<school_name>', methods=['GET'])
@jwt_required()
def get_school_map(school_name):
    
    # Convert school name to a filename-friendly format (e.g., remove spaces and convert to lowercase)
    sanitized_school_name = secure_filename(school_name)

    # Define the directory path where map files are stored
    maps_directory = './tmp/maps'  # Update this with the actual directory path

    # List of supported file extensions to try
    file_extensions = ['.pdf', '.png', '.jpg', '.jpeg']

    # Try to find the map file with supported extensions
    for ext in file_extensions:
        filename = sanitized_school_name + ext
        #logs more logs
        print("PRINT ALL FILES")
        print(os.listdir("./tmp/maps"))
        map_path = os.path.join(maps_directory, filename)
        print(map_path)
        if os.path.exists(map_path):
            # Serve the map file with the found extension
            return send_file(map_path)

    return 'Map file not found', 404
def create_initial_user():
    # Add the user if not already present in the database
    existing_user = User.query.filter_by(email='joshkim2805@gmail.com').first()
    if not existing_user:
        new_user = User(email='joshkim2805@gmail.com', name='Joshua Kim')
        db.session.add(new_user)
        db.session.commit()
    existing_coach = Coach.query.filter_by(email='joshkim771@gmail.com').first()
    if not existing_coach:
        new_coach = Coach(email='joshkim771@gmail.com', name='Joshua Kim', school_id=1)
        db.session.add(new_coach)
        db.session.commit()
    
    existing_admin = Admin.query.filter_by(email='joshkim771@gmail.com').first()
    if not existing_admin:
        new_admin = Admin(email='joshkim771@gmail.com', name='Joshua Kim')
        db.session.add(new_admin)
        db.session.commit()

        # Add the user if not already present in the database
    existing_user = User.query.filter_by(email='rjoshi6@gmail.com').first()
    if not existing_user:
        new_user = User(email='rjoshi6@gmail.com', name='Rohan Joshi')
        db.session.add(new_user)
        db.session.commit()
    existing_coach = Coach.query.filter_by(email='rjoshi6@gmail.com').first()
    if not existing_coach:
        new_coach = Coach(email='rjoshi6@gmail.com', name='Rohan Joshi', school_id=1)
        db.session.add(new_coach)
        db.session.commit()
    
    existing_admin = Admin.query.filter_by(email='rjoshi6@gmail.com').first()
    if not existing_admin:
        new_admin = Admin(email='rjoshi6@gmail.com', name='Rohan Joshi')
        db.session.add(new_admin)
        db.session.commit()
        #

    # Add the user if not already present in the database
    existing_user = User.query.filter_by(email='asenkran69@gmail.com').first()
    if not existing_user:
        new_user = User(email='asenkran69@gmail.com', name='Christopher Rodriguez')
        db.session.add(new_user)
        db.session.commit()
    existing_coach = Coach.query.filter_by(email='asenkran69@gmail.com').first()
    if not existing_coach:
        new_coach = Coach(email='asenkran69@gmail.com', name='Christopher Rodriguez', school_id=1)
        db.session.add(new_coach)
        db.session.commit()
    
    existing_admin = Admin.query.filter_by(email='asenkran69@gmail.com').first()
    if not existing_admin:
        new_admin = Admin(email='asenkran69@gmail.com', name='Christopher Rodriguez')
        db.session.add(new_admin)
        db.session.commit()

           
    existing_user = User.query.filter_by(email='jadenmanuel2006@gmail.com').first()
    if not existing_user:
        new_user = User(email='jadenmanuel2006@gmail.com', name='Jaden Manuel')
        db.session.add(new_user)
        db.session.commit()
    existing_coach = Coach.query.filter_by(email='jadenmanuel2006@gmail.com').first()
    if not existing_coach:
        new_coach = Coach(email='jadenmanuel2006@gmail.com', name='Jaden Manuel', school_id=1)
        db.session.add(new_coach)
        db.session.commit()
    
    existing_admin = Admin.query.filter_by(email='jadenmanuel2006@gmail.com').first()
    if not existing_admin:
        new_admin = Admin(email='jadenmanuel2006@gmail.com', name='Jaden Manuel')
        db.session.add(new_admin)
        db.session.commit()
        #

create_initial_user()

# @app.before_request
# def handle_preflight():
#     if request.method == "OPTIONS":
#         res = Response()
#         res.headers['Access-Control-Allow-Credentials'] = 'true'
#         return res


@app.route('/', defaults={'path': ''})
@app.route('/<path:path>')
def catch_all(path):
    return render_template('index.html')


@app.route('/', methods=['GET'])
def hello_world():
    cmd = ['java', '-cp', '.', 'helloworld']

    # Start the Java process
    process = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

    # Send the input data and read the output
    output, errors = process.communicate()


    process.stdout.close()
    process.stderr.close()

    # Wait for the process to finish
    process.wait()

    # Check for errors
    if process.returncode != 0:
        return jsonify({"error": "Java program execution failed", "details": errors}), 500
    return jsonify({"output": output}), 200



    
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
    
    email_domain = "*@" + user_info['email'].split('@')[-1]
    
    user = User.query.filter_by(email=user_info['email']).first()
    if (user is None):
        user = User.query.filter_by(email=email_domain).first()
    coach = Coach.query.filter_by(email=user_info['email']).first()
    admin = Admin.query.filter_by(email=user_info['email']).first()
    if (user is not None) or (coach is not None) or (admin is not None):
        # Include role in the user_info JSON
        role = "user"
        
        if (isCoach(user_info['email'])):
            role = "coach"
        if (isAdmin(user_info['email'])):
            role = "admin"
    
        user_info['role'] = role
##
        # Create JWT token and send response with user_info including role
        jwt_token = create_access_token(identity=user_info['email'])
        response = jsonify(user=user_info, role=role)
        #response.set_cookie('access_token_cookie', value=jwt_token, secure=True)
    
        response.set_cookie('access_token_cookie', value=jwt_token, secure=True, httponly=True, samesite='None', domain="rohanjoshi.dev")
        #
        return response, 200
    else:
       #
        response = jsonify({'message':'Failed'})
        return response, 401

    

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
    response.delete_cookie('access_token_cookie', domain='.rohanjoshi.dev')
    # response.delete_cookie('logged_in')
    return response, 200

def isCoach(email):
    coach = Coach.query.filter_by(email=email).first()
    if not coach:
        return False
    return True

def isAdmin(email):
    admin = Admin.query.filter_by(email=email).first()
    if not admin:
        return False
    return True

@app.route("/protected", methods=["GET"])
@jwt_required()
def protected():
    #every user has access
    jwt_token = request.cookies.get('access_token_cookie')
    current_user = get_jwt_identity()
    #^this is the email of the user
    # return isCoach("joshkim771@gmail.com")
    return "Success", 200

@app.route("/protected_coach", methods=["GET"])
@jwt_required()
def protected_coach():
    #only coach or above (coach, admin) have access
    jwt_token = request.cookies.get('access_token_cookie')
    current_user = get_jwt_identity()
    coach = isCoach(current_user)
    if not coach:
        return "Unauthorized", 401
    return "Success", 200


@app.route("/protected_admin", methods=["GET"])
@jwt_required()
def protected_admin():
    jwt_token = request.cookies.get('access_token_cookie')
    current_user = get_jwt_identity()
    admin = isAdmin(current_user)
    if not admin:
        return "Unauthorized", 401
    return "Success", 200


#Route to increment request count
@app.route('/increment', methods=['POST'])
def increment():
    counter = RequestCount.query.get(1)
    if counter:
        counter.count += 1
        db.session.commit()
    return jsonify({"message": "Request count incremented", "currentCount": counter.count}), 200

#Route to increment request count

# class SaveAdminEmail(FlaskForm):
#     name = StringField('name', validators=[DataRequired()])
#     email = StringField('email', validators=[DataRequired()])
#     submit = SubmitField('Save')

@app.route('/save_admin_email', methods=['POST'])
# @jwt_required()
def save_admin_email():
    
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)
    # if not admin:
    #     return "Unauthorized", 401
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    if not admin:
        return "Unauthorized", 401
    name = request.form['name']
    email = request.form['email']

    admin = Admin.query.filter_by(email=email).first()
    if admin == None:
        admin = Admin(name=name, email=email)
        db.session.add(admin)
        db.session.commit()
    else:
        admin.name = name
        db.session.commit()
    
    message = Mail(
    from_email='testdebateteamapp@gmail.com',
    to_emails=email,
    subject='Welcome to the Debate Team Dashboard!',
    html_content='<p>Hi, ' + name + '!</p><p>You were added as an admin to the Debate Team Dashboard.</p><strong>To access the dashboard, go to: rohanjoshi.dev :)</strong>')
    
    try:
        sg = SendGridAPIClient(os.environ.get('SENDGRID_API_KEY'))
        response = sg.send(message)
        print(response.status_code)
        print(response.body)
        print(response.headers)
    except Exception as e:
        print(e.message)
    return "Success", 200


@app.route('/save_coach_email', methods=['POST'])
# @jwt_required()
def save_coach_email():
    
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)
    # if not admin:
    #     return "Unauthorized", 401
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    if not admin:
        return "Unauthorized", 401
    name = request.form['name']
    email = request.form['email']
    school = request.form['school']
    
    #get school id
    coach = Coach.query.filter_by(email=email).first()
    if coach == None:
        coach = Coach(name=name, email=email, school_id = 1)
        db.session.add(coach)
        db.session.commit()
    else:
        coach.name = name
        db.session.commit()
    
    message = Mail(
    from_email='testdebateteamapp@gmail.com',
    to_emails=email,
    subject='Welcome to the Debate Team Dashboard!',
    html_content='<p>Hi, ' + name + '!</p><p>You were added as a coach to the Debate Team Dashboard.</p><strong>To access the dashboard, go to: rohanjoshi.dev :)</strong>')
    #
    try:
        sg = SendGridAPIClient(os.environ.get('SENDGRID_API_KEY'))
        response = sg.send(message)
        print(response.status_code)
        print(response.body)
        print(response.headers)
    except Exception as e:
        print(e.message)
    return "Success", 200

@app.route('/save_user_email', methods=['POST'])
# @jwt_required()
def save_user_email():
    
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)
    # if not admin:
    #     return "Unauthorized", 401
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    if not admin:
        return "Unauthorized", 401
    name = request.form['name']
    email = request.form['email']
#
    user = User.query.filter_by(email=email).first()
    if user == None:
        user = User(name=name, email=email)
        db.session.add(user)
        db.session.commit()
    else:
        user.name = name
        db.session.commit()
    
    # message = Mail(
    # from_email='testdebateteamapp@gmail.com',
    # to_emails=email,
    # subject='Welcome to the Debate Team Dashboard!',
    # html_content='<p>Hi, ' + name + '!</p><p>You were added as a user to the Debate Team Dashboard.</p><strong>To access the dashboard, go to: rohanjoshi.dev :)</strong>')
    
    # try:
    #     sg = SendGridAPIClient(os.environ.get('SENDGRID_API_KEY'))
    #     response = sg.send(message)
    #     print(response.status_code)
    #     print(response.body)
    #     print(response.headers)
    # except Exception as e:
    #     print(e.message)
    return "Success", 200
@app.route("/deleteuser", methods=["POST"])
# @jwt_required()
def deleteuser():
    jwt_token = request.cookies.get('access_token_cookie')
    
    admin = isAdmin(decode_token(jwt_token)['sub'])

    if not admin:
        return "Unauthorized", 401
    
    #
    
    #
    # jwt_token = request.cookies.get('access_token_cookie')
    # admin = isAdmin(current_user)
    # if not admin:
    #     return "Unauthorized", 401
    # email = request.form['email']
    email = request.get_json()['email']
    user = User.query.filter_by(email=email).first()
    coach = Coach.query.filter_by(email=email).first()
    admin = Admin.query.filter_by(email=email).first()
    if user != None:
        db.session.delete(user)
        db.session.commit()
    if coach != None:
        db.session.delete(coach)
        db.session.commit()
    if admin != None:
        #add check, admin cannot delete themselves
        db.session.delete(admin)
        db.session.commit()
        

    return "Success", 200




@app.route("/deletecoach", methods=["POST"])
# @jwt_required()
def deletecoach():
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)
    # if not admin:
    #     return "Unauthorized", 401
    
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    if not admin:
        return "Unauthorized", 401
    email = request.form['email']
    email = request.get_json()['email']
    user = Coach.query.filter_by(email=email).first()
    if user != None:
        db.session.delete(user)
        db.session.commit()
    return "Success", 200

@app.route('/users', methods=['GET'])
@jwt_required()
def get_users():
    jwt_token = request.cookies.get('access_token_cookie')
    current_user = get_jwt_identity()
    admin = isAdmin(current_user)
    if not admin:##
        return "Unauthorized access", 401
    
    users = User.query.all()
    user_data = []
    for user in users:
        user_info = {'name' : user.name, 'email' : user.email}
        user_data.append(user_info)
    return jsonify(user_data),200


@app.route('/coaches', methods=['GET'])
@jwt_required()
def get_coaches():
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)
    # if not admin:
    #     return "Unauthorized", 401
    current_user = get_jwt_identity()
    admin = isAdmin(current_user)
    if not admin:##
        return "Unauthorized access", 401
    users = Coach.query.all()
    user_data = []
    for user in users:
        user_info = {'name' : user.name, 'email' : user.email}
        user_data.append(user_info)
    return jsonify(user_data),200


@app.route('/admins', methods=['GET'])
@jwt_required()
def get_admins():
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)
    # if not admin:
    #     return "Unauthorized", 401
    current_user = get_jwt_identity()
    admin = isAdmin(current_user)
    if not admin:##
        return "Unauthorized access", 401
    users = Admin.query.all()
    user_data = []
    for user in users:
        user_info = {'name' : user.name, 'email' : user.email}
        user_data.append(user_info)
    return jsonify(user_data),200

@app.route('/schools', methods=['GET'])
@jwt_required()
def get_schools():
    schools = School.query.all()
    school_data = [{
        'id': school.id,
        'name': school.name,
        'num_debaters': school.num_debaters,
        'num_judges': school.num_judges
    } for school in schools]
    return jsonify(school_data), 200

@app.route('/tournaments')
@jwt_required()
def get_tournaments():

    tournaments = Tournament.query.all()

    tournaments_list = []
    for tournament in tournaments:
        school_data = []
        for school in tournament.schools:
            school_data.append({
                "name": school.name,
                "num_judges": school.num_judges
            })

        tournament_data = {
            "id": tournament.id,
            "datetime": tournament.datetime.isoformat() if tournament.datetime else None,
            "host_school": {
                "id": tournament.host_school_id,
                "name": School.query.get(tournament.host_school_id).name
            },
            "schools": school_data
        }
        tournaments_list.append(tournament_data)

    return jsonify(tournaments_list), 200

@app.route('/tournament/<int:tournament_id>')
@jwt_required()
def get_tournament(tournament_id):
    tournament = Tournament.query.get(tournament_id)
    if tournament is None:
        return jsonify({"message": "Tournament not found"}), 404

    school_data = []
    for school in tournament.schools:
        coach = school.coach
        coach_name = coach.name if coach else "No coach assigned"

        school_data.append({
            "name": school.name,
            "num_debaters": school.num_debaters,
            "num_judges": school.num_judges,
            "coach": coach_name,
            "id": school.id
        })

    tournament_data = {
        "id": tournament.id,
        "datetime": tournament.datetime.isoformat() if tournament.datetime else None,
        "host_school": {
            "id": tournament.host_school_id,
            "name": School.query.get(tournament.host_school_id).name
        },
        "schools": school_data
    }

    return jsonify(tournament_data)


@app.route('/tournamentschedule/<int:tournament_id>', methods=['GET'])
@jwt_required()
def get_tournament_schedule(tournament_id):
    letters_to_numbers = {chr(i): i - ord('A') for i in range(ord('A'), ord('Z') + 1)}
    # Fetch the tournament
    tournament = Tournament.query.get(tournament_id)

    if tournament is None:
        return jsonify({"message": "Tournament not found"}), 404
    else:
        matches = Match.query.filter_by(tournament_id=tournament_id).order_by(Match.id).all()
        if matches:
            match_export_list = []
            for match in matches:
                match_round = match.round_number
                if len(match_export_list)<=match_round-1:
                    match_export_list.append([])
                match_affirmative = str(letters_to_numbers[match.affirmative[0]]) + "~" + str(int(match.affirmative[1]) - 1)
                match_negative = str(letters_to_numbers[match.negative[0]]) + "~" + str(int(match.negative[1]) - 1)
                match_judge = "J" + str(letters_to_numbers[match.judge[0]]) + "~" + str(int(match.judge[2]) - 1)
                full_match = match_affirmative + "|" + match_negative + "|" + match_judge
                match_export_list[match_round-1].append(full_match)
            return match_export_list

    # Initialize lists to hold player and judge counts
    players_counts = []
    judges_counts = []

    # Iterate through schools participating in the tournament
    for school in tournament.schools:
        players_counts.append(school.num_debaters)
        judges_counts.append(school.num_judges)

    # Convert the lists to comma-separated strings
    players_str = ",".join(map(str, players_counts))
    judges_str = ",".join(map(str, judges_counts))

    # Construct the command to run your Java program
    
    # Compile the Java program
    try:
        compile_process = run(['javac', 'algorithm3.java'], check=True, stderr=PIPE, text=True)
    except CalledProcessError as e:
        return jsonify({"error": "Java compilation failed", "details": e.stderr}), 500
    
    cmd = ['java', '-cp', '.', 'algorithm3']

    # Start the Java process
    print("A")
    process = subprocess.Popen(cmd, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

    print("B")
    # Send the input data and read the output
    output, errors = process.communicate(input=f"{players_str}\n{judges_str}\n")
    
    # Close the stdin, stdout, and stderr streams
    process.stdin.close()
    process.stdout.close()
    process.stderr.close()

    # Wait for the process to finish
    process.wait()
    print("C")
    # Check for errors
    if process.returncode != 0:
        print("D")
        print(errors)
        return jsonify({"error": "Java program execution failed", "details": errors}), 500
    print("E")
    # Process the output back into a Python list of lists (or any other desired structure)
    matches = [line.split(",") for line in output.strip().split("\n")]
    print("F")
    return jsonify(matches)

@app.route('/varsitytournamentschedule/<int:tournament_id>', methods=['POST'])
# @jwt_required()
def get_varsity_tournament_schedule(tournament_id):
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    coach = isCoach(curr_user)
    if not admin and not coach:
        return "Unauthorized", 401
    letters_to_numbers = {chr(i): i - ord('A') for i in range(ord('A'), ord('Z') + 1)}
    # Fetch the tournament
    tournament = Tournament.query.get(tournament_id)

    # Initialize lists to hold player and judge counts
    players_counts = []
    judges_counts = []

    # Iterate through schools participating in the tournament
    for school in tournament.schools:
        players_counts.append(school.num_debaters)
        judges_counts.append(school.num_judges)

    # Convert the lists to comma-separated strings
    players_str = ",".join(map(str, players_counts))
    judges_str = ",".join(map(str, judges_counts))
    previous_wins = request.json['previous_wins']

    # Construct the command to run your Java program
    
    # Compile the Java program
    try:
        if os.path.split(os.getcwd())[1] != "Varsity Algorithm":
            os.chdir("Varsity Algorithm")
        compile_process = run(['javac', 'Base/Testing.java'], check=True, stderr=PIPE, text=True)
    except CalledProcessError as e:
        return jsonify({"error": "Java compilation failed", "details": e.stderr}), 500
    
    cmd = ['java', '-cp', '.', 'Base/Testing']

    # Start the Java process
    process = subprocess.Popen(cmd, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

    # Send the input data and read the output
    output, errors = process.communicate(input=f"{players_str}\n{judges_str}\n{previous_wins}\n")
    
    # Close the stdin, stdout, and stderr streams
    process.stdin.close()
    process.stdout.close()
    process.stderr.close()

    # Wait for the process to finish
    process.wait()
    # Check for errors
    if process.returncode != 0:
        print(errors)
        os.chdir("..")
        return jsonify({"error": "Java program execution failed", "details": errors}), 500
    # Process the output back into a Python list of lists (or any other desired structure)
    os.chdir("..")
    matches = [line.split(",") for line in output.strip().split("\n")]
    return jsonify(matches)

# class UpdateSchoolForm(FlaskForm):
#     pairs = IntegerField('Number of pairs', validators=[DataRequired()])
#     judges = IntegerField('Number of Judges', validators=[DataRequired()])
#     submit = SubmitField('Update School')

@app.route('/updateschool/<int:school_id>', methods=['POST'])
# @jwt_required()
def update_school(school_id):
    
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)
    # coach = isCoach(current_user)
    # if not admin and not coach:
    #     return "Unauthorized", 401
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    coach = isCoach(curr_user)
    if not admin and not coach:
        return "Unauthorized", 401
    
    
    school = School.query.get(school_id)
    if not school:
        return jsonify({"message": "School not found"}), 404
    num_debaters = int(request.form['pairs'])
    num_judges = int(request.form['judges'])
    
    tournaments = Tournament.query.filter(
        (Tournament.host_school_id == school_id) | 
        (Tournament.schools.any(id=school_id))
    ).all()

    tournament_ids = [tournament.id for tournament in tournaments]

    matches = Match.query.filter(Match.tournament_id.in_(tournament_ids)).all()

    for match in matches:
        db.session.delete(match)

    db.session.commit()
    
    print(num_debaters)
    print(num_judges)


    school.num_debaters = num_debaters
    school.num_judges = num_judges

    db.session.commit()

    return "Success", 200

@app.route('/updateschoolcoach/<int:school_id>', methods=['PUT'])
# @jwt_required()
def update_school_coach(school_id):
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)

    # if not admin:
    #     return "Unauthorized", 401
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    coach = isCoach(curr_user)
    if not admin and not coach:
        return "Unauthorized", 401
    school = School.query.get(school_id)
    if not school:
        return jsonify({"message": "School not found"}), 404

    data = request.get_json()
    if 'coach_id' in data:
        coach_id = data['coach_id']
        coach = Coach.query.get(coach_id)
        if coach:
            school.coach = coach
            db.session.commit()
            return jsonify({"message": "Coach updated successfully"}), 200
        else:
            return jsonify({"message": "Coach not found"}), 404
    else:
        return jsonify({"message": "Coach ID not provided"}), 400
    
# class TournamentForm(FlaskForm):
#     name = StringField('Tournament Name', validators=[DataRequired()])
#     datetime = DateField('Date', validators=[DataRequired()])
#     host_school = SelectField('Host School', coerce=int, validators=[DataRequired()]) 
#     submit = SubmitField('Create Tournament')

#     def __init__(self, *args, **kwargs):
#         super(TournamentForm, self).__init__(*args, **kwargs)
#         self.host_school.choices = [(school.id, school.name) for school in School.query.all()]
    
@app.route('/add_tournament', methods=['POST'])
# @jwt_required()
def add_tournament():
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)
    # coach = isCoach(current_user)
    # if not admin and not coach:
    #     return "Unauthorized", 401
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    coach = isCoach(curr_user)
    if not admin and not coach:
        return "Unauthorized", 401

    data = request.get_json()
    try:
        tournament_date = datetime.strptime(data['datetime'], '%Y-%m-%d').replace(hour=0, minute=0)
        new_tournament = Tournament(
            host_school_id=data['host_school_id'],
            datetime=tournament_date
        )
        db.session.add(new_tournament)
        db.session.commit()

        for school_id in data['schools']:
            db.session.execute(tournament_school.insert().values(tournament_id=new_tournament.id, school_id=school_id))
        
        db.session.commit()

        return jsonify({"message": "Tournament added successfully", "tournament_id": new_tournament.id}), 200

    except Exception as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 500
    
    ####

# class AddSchoolForm(FlaskForm):
#     name = StringField('School Name', validators=[DataRequired()])
#     num_debaters = IntegerField('Number of Debaters', validators=[DataRequired()])
#     num_judges = IntegerField('Number of Judges', validators=[DataRequired()])
#     submit = SubmitField('Add School')

@app.route('/add_school', methods=['POST'])
def add_school():
    
    jwt_token = request.cookies.get('access_token_cookie')
    admin = isAdmin(decode_token(jwt_token)['sub'])
    
    if not admin:
        return "Unauthorized", 401
 
    data = request.get_json()
    try:
        new_school = School(
            name=data['name'],
            num_debaters=data['num_debaters'],
            num_judges=data['num_judges']
        )
        db.session.add(new_school)
        db.session.commit()

        return jsonify({"message": "School added successfully", "school_id": new_school.id}), 200

    except Exception as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 500
    
@app.route("/delete_school", methods=["POST"])
# @jwt_required()
def deleteschool():
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)

    if not admin:
        return "Unauthorized", 401
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)
    # if not admin:
    #     return "Unauthorized", 401
    name = request.get_json()['name']
    school = School.query.filter_by(name=name).first()
    if school != None:
        db.session.delete(school)
        db.session.commit()
    return "Success", 200

@app.route('/updateschoolranking', methods=['POST'])
# @jwt_required()
def update_school_ranking():
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)
    # if not admin:
    #     return "Unauthorized", 401
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    if not admin:
        return "Unauthorized", 401
    data = request.get_json()
    rankings = data['rankings']
    for rank, school_name in enumerate(rankings, start=1):
        school = School.query.filter_by(name=school_name).first()
        if school:
            school.ranking = rank
            db.session.commit()
    return "Success", 200

@app.route('/updatepartnershipranking', methods=['POST'])
# @jwt_required()
def update_partnership_ranking():
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)
    # if not admin:
    #     return "Unauthorized", 401
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    if not admin:
        return "Unauthorized", 401
    data = request.get_json()
    rankings = data['rankings']
    for rank, partnership_info in enumerate(rankings, start=1):
        partnership_name, school_name = partnership_info.split(' (')
        school_name = school_name[:-1]  # Remove the trailing bracket
        partnership = Partnership.query.filter_by(name=partnership_name).first()
        if partnership:
            partnership.ranking = rank
            db.session.commit()
    return "Success", 200

@app.route('/deletetournament', methods=['POST'])
# @jwt_required()
def delete_tournament():
    # jwt_token = request.cookies.get('access_token_cookie')
    # current_user = get_jwt_identity()
    # admin = isAdmin(current_user)
    # coach = isCoach(current_user)
    # if not admin and not coach:
    #     return "Unauthorized", 401
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    coach = isCoach(curr_user)
    if not admin and not coach:
        return "Unauthorized", 401
    try:#
        data = request.get_json()
        tournament_id = data.get('tournamentid')
##
        if tournament_id is None:
            return jsonify({"error": "Tournament ID not provided"}), 400

        tournament = Tournament.query.get(tournament_id)

        if tournament is None:
            return jsonify({"error": "Tournament not found"}), 404

        # Delete the tournament from the database
        db.session.delete(tournament)
        db.session.commit()

        return jsonify({"message": "Tournament deleted successfully"}), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500
    
#

@app.route('/tournament/<int:tournament_id>/rooms', methods=['POST'])
def update_room_assignments(tournament_id):
    jwt_token = request.cookies.get('access_token_cookie')
    curr_user = decode_token(jwt_token)['sub']
    admin = isAdmin(curr_user)
    coach = isCoach(curr_user)
    if not admin and not coach:
        return "Unauthorized", 401
    try:
        room_assignments_data = request.get_json()
        if not isinstance(room_assignments_data, list):
            return jsonify({'error': 'Invalid data format'}), 400

        RoomAssignment.query.filter_by(tournament_id=tournament_id).delete()

        for room_assignment in room_assignments_data:
            match_index = room_assignment.get('match_index')
            room_number = room_assignment.get('room_number')
            if room_number:  
                new_room_assignment = RoomAssignment(
                    tournament_id=tournament_id,
                    match_index=match_index,
                    room_number=room_number
                )
                db.session.add(new_room_assignment)
        db.session.commit()
        return jsonify({'message': 'Room assignments success'}), 200
    except Exception as e:
        db.session.rollback()
        return jsonify({'error': 'Failed to update room assignments', 'details': str(e)}), 500

@app.route('/tournament/<int:tournament_id>/update_schedule', methods=['POST'])
def update_tournament_schedule(tournament_id):
    try:
        # Extract the schedule data from the request
        schedule_data = request.get_json().get('schedule')
        if not schedule_data:
            return jsonify({"error": "No schedule data provided"}), 400


        # Clear existing matches for this tournament
        Match.query.filter_by(tournament_id=tournament_id).delete()

        db.session.commit()

        # Add new matches based on the received schedule data
        for round_index, round in enumerate(schedule_data):
            for match_data in round:
                new_match = Match(
                    tournament_id=tournament_id,
                    round_number=round_index + 1,
                    affirmative=match_data['affirmative'],
                    negative=match_data['negative'],
                    judge=match_data['judge']
                )
                db.session.add(new_match)

        db.session.commit()
        return jsonify({"message": "Schedule updated successfully"}), 200
    
    except Exception as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 500


@app.route('/tournament/<int:tournament_id>/rooms', methods=['GET'])
@jwt_required()
def get_room_assignments(tournament_id):
    room_assignments = RoomAssignment.query.filter_by(tournament_id=tournament_id).all()
    room_assignments_data = [{'match_index': ra.match_index, 'room_number': ra.room_number} for ra in room_assignments]
    return jsonify(room_assignments_data), 200



if __name__ == '__main__':
    app.run(debug=True, port=5000)



import os
import subprocess

import requests
from flask import Flask, jsonify, request, Response, render_template
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
from flask_migrate import Migrate

from sendgrid import SendGridAPIClient
from sendgrid.helpers.mail import Mail

import subprocess
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///database.db'
db = SQLAlchemy(app)

app.app_context().push()
# CORS(app, origins=['http://localhost:3000'], supports_credentials=True)
# CORS(app, origins=['http://localhost:3000', 'https://test-debate-frontend-update-deploy.onrender.com', 'https://debate-app-backend.onrender.com'], supports_credentials=True)
CORS(app, resources={r"/*": {"origins": "https://www.rohanjoshi.dev", "supports_credentials": True}})

migrate = Migrate(app, db)

load_dotenv()

app.config["JWT_COOKIE_SECURE"] = True
app.config['JWT_SECRET_KEY'] = os.environ['JWT_SECRET_KEY']
app.config['JWT_TOKEN_LOCATION'] = ['cookies']
app.config["JWT_ACCESS_TOKEN_EXPIRES"] = timedelta(hours=1)
app.config["JWT_COOKIE_SAMESITE"] = "None"
app.config["JWT_COOKIE_DOMAIN"] = "rohanjoshi.dev"
jwt = JWTManager(app)

GOOGLE_CLIENT_ID = os.environ['GOOGLE_CLIENT_ID']
GOOGLE_CLIENT_SECRET = os.environ['GOOGLE_CLIENT_SECRET']



#Define the RequestCount model
class RequestCount(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    count = db.Column(db.Integer, default=0)

class User(db.Model):
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
    name = db.Column(db.Integer)
    email = db.Column(db.String, unique=True)
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

db.create_all()

#db model creation tests
"""
if RequestCount.query.get(1) is None:
    new_counter = RequestCount(id=1, count=0)
    db.session.add(new_counter)
    db.session.commit()

db.session.query(School).delete()
db.session.query(Tournament).delete()

school1 = School(id=1, name="Bergen Academies", num_debaters=2, num_judges=2)
school2 = School(id=2, name="Mountain Valley", num_debaters=2, num_judges=2)
school3 = School(id=3, name="Bridgewater High", num_debaters=3, num_judges=1)

db.session.add(school1)
db.session.add(school2)
db.session.add(school3)
db.session.commit()

tournament = Tournament(id=1, host_school_id=school1.id, datetime=datetime(2024, 2, 5, 17, 0))
db.session.add(tournament)
db.session.commit()

tournament.schools.append(school1)
tournament.schools.append(school2)
tournament.schools.append(school3)
db.session.commit()
"""

def create_initial_user():
    # Add the user if not already present in the database
    existing_user = User.query.filter_by(email='joshkim771@gmail.com').first()
    if not existing_user:
        new_user = User(email='joshkim771@gmail.com', name='Joshua Kim')
        db.session.add(new_user)
        db.session.commit()
        
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

    print(user_info['email'])
    #check if user is in database, if not add the user 
    #FYI FOR LATER ON: MAYBE ONLY ALLOW LOGIN IF USER IS AN EXISTING USER; BC ONLY ADMINS CAN ACCESS
    user = User.query.filter_by(email=user_info['email']).first()

    if user is not None:
        jwt_token = create_access_token(identity=user_info['email'])  
        response = jsonify(user=user_info)
        response.set_cookie('access_token_cookie', value=jwt_token, secure=True, httponly=True, samesite='None', domain="rohanjoshi.dev")
        # response.set_cookie('logged_in', value="yes", secure=True, httponly=True, samesite='None', domain="test-debate-frontend-update-deploy.onrender.com")
        return response, 200
    else:
       
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
    name = request.form['name']
    email = request.form['email']

    user = User.query.filter_by(email=email).first()
    if user == None:
        user = User(name=name, email=email)
        db.session.add(user)
        db.session.commit()
    else:
        user.name = name
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

@app.route("/deleteuser", methods=["POST"])
def deleteuser():

    # email = request.form['email']
    email = request.get_json()['email']
    user = User.query.filter_by(email=email).first()
    if user != None:
        db.session.delete(user)
        db.session.commit()
    return "Success", 200

@app.route('/users', methods=['GET'])
def get_users():
    users = User.query.all()
    user_data = []
    for user in users:
        user_info = {'name' : user.name, 'email' : user.email}
        user_data.append(user_info)
    return jsonify(user_data),200

@app.route('/schools', methods=['GET'])
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
def get_tournament_schedule(tournament_id):
    # Fetch the tournament
    tournament = Tournament.query.get(tournament_id)

    if tournament is None:
        return jsonify({"message": "Tournament not found"}), 404

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
    
    cmd = ['java', '-cp', '.', 'algorithm2']

    # Start the Java process
    process = subprocess.Popen(cmd, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

    # Send the input data and read the output
    output, errors = process.communicate(input=f"{players_str}\n{judges_str}\n")

    # Close the stdin, stdout, and stderr streams
    process.stdin.close()
    process.stdout.close()
    process.stderr.close()

    # Wait for the process to finish
    process.wait()

    # Check for errors
    if process.returncode != 0:
        return jsonify({"error": "Java program execution failed", "details": errors}), 500

    # Process the output back into a Python list of lists (or any other desired structure)
    matches = [line.split(",") for line in output.strip().split("\n")]

    return jsonify(matches)

@app.route('/updateschool/<int:school_id>', methods=['POST'])
def update_school(school_id):
    school = School.query.get(school_id)
    if not school:
        return jsonify({"message": "School not found"}), 404
    num_debaters = int(request.form['pairs'])
    num_judges = int(request.form['judges'])
    
    
    print(num_debaters)
    print(num_judges)


    school.num_debaters = num_debaters
    school.num_judges = num_judges

    db.session.commit()

    return "Success", 200

@app.route('/updateschoolcoach/<int:school_id>', methods=['PUT'])
def update_school_coach(school_id):
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
    
@app.route('/add_tournament', methods=['POST'])
def add_tournament():
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
    
    #
@app.route('/add_school', methods=['POST'])
def add_school():
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
def deleteschool():
    name = request.get_json()['name']
    school = School.query.filter_by(name=name).first()
    if school != None:
        db.session.delete(school)
        db.session.commit()
    return "Success", 200

@app.route('/updateschoolranking', methods=['POST'])
def update_school_ranking():
    data = request.get_json()
    rankings = data['rankings']
    for rank, school_name in enumerate(rankings, start=1):
        school = School.query.filter_by(name=school_name).first()
        if school:
            school.ranking = rank
            db.session.commit()
    return "Success", 200

@app.route('/updatepartnershipranking', methods=['POST'])
def update_partnership_ranking():
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
def delete_tournament():
    try:
        data = request.get_json()
        tournament_id = data.get('tournamentid')

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
    
if __name__ == '__main__':
    app.run(debug=True, port=5000)
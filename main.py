from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///database.db'
db = SQLAlchemy(app)

app.app_context().push()

CORS(app)

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
    name = request.form["name"]
    email = request.form["email"]
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
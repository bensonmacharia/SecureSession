import os
import time
import jwt
import secrets
from flask import Flask, request, abort, jsonify, make_response, g
from flask_sqlalchemy import SQLAlchemy
from marshmallow import fields
from marshmallow_sqlalchemy import ModelSchema
from flask_httpauth import HTTPBasicAuth
from werkzeug.security import generate_password_hash, check_password_hash

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://bmacharia:12qwaszxasqw12@localhost:3306/SecureSession'
app.config['SECRET_KEY'] = 'Begin at the beginning, the King said, very gravely, and go on till you come to the end: then stop. -  Lewis Carroll, Alice in Wonderland'
app.config['SQLALCHEMY_COMMIT_ON_TEARDOWN'] = True

db = SQLAlchemy(app)
auth = HTTPBasicAuth()

# Model
class User(db.Model):
    __tablename__ = "users"
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    username = db.Column(db.String(100), unique=True, nullable=False)
    password = db.Column(db.String(255), nullable=False)
    fullname = db.Column(db.String(255), nullable=False)
    email = db.Column(db.String(100), unique=True, nullable=False)
    slug = db.Column(db.String(255), unique=True)
    access_token = db.Column(db.String())

    # Hash password
    def hash_password(self, password):
        self.password = generate_password_hash(password)

    # Verify hashed password at login
    def verify_password(self, password):
        return check_password_hash(self.password, password)

    # Generate JWT token
    def generate_auth_token(self, expires_in=600):
        return jwt.encode(
            {'username': self.username, 'exp': time.time() + expires_in},
            app.config['SECRET_KEY'], algorithm='HS256')

    # Verify token validatity
    @staticmethod
    def verify_auth_token(token):
        try:
            data = jwt.decode(token, app.config['SECRET_KEY'],
                              algorithms=['HS256'])
        except jwt.ExpiredSignatureError:
            return False
        return User.query.get(data['username'])

    def create(self):
        db.session.add(self)
        db.session.commit()
        return self

    def __init__(self, username, password, fullname, email):
        self.username = username
        self.access_token = jwt.encode({'username': username, 'exp': time.time(
        ) + 600}, app.config['SECRET_KEY'], algorithm='HS256')
        self.slug = secrets.token_hex(16)
        self.password = generate_password_hash(password)
        self.fullname = fullname
        self.email = email

    def __repr__(self):
        return f"{self.id}"


db.create_all()


class UserSchema(ModelSchema):
    class Meta(ModelSchema.Meta):
        model = User
        sqla_session = db.session

    username = fields.String(required=True)
    password = fields.String(required=True)
    fullname = fields.String(required=True)
    email = fields.String(required=True)

# User registration
@app.route('/api/v1/user/register', methods=['POST'])
def new_user():
    username = request.json.get('username')
    password = request.json.get('password')
    fullname = request.json.get('fullname')
    email = request.json.get('email')

    # Validate all values are filles
    if username is None or password is None or fullname is None or email is None:
        response = jsonify(
            {"status": 0, "message": "all values must be filled"})
        return make_response(response)

    # check if username is already taken
    if User.query.filter_by(username=username).first() is not None:
        response = jsonify({"status": 0, "message": "username must be unique"})
        return make_response(response)

    # check if email already exists
    if User.query.filter_by(email=email).first() is not None:
        response = jsonify({"status": 0, "message": "email must be unique"})
        return make_response(response)

    data = request.get_json()
    user_schema = UserSchema()
    user = user_schema.load(data)
    result = user_schema.dump(user.create())
    response = jsonify(
        {"status": 1, "message": "user registered", "user": result})

    return make_response(response)

# User login
@auth.verify_password
@app.route('/api/v1/user/login', methods=['POST'])
def login():
    # authenticate by username and password
    username = request.json.get('username')
    password = request.json.get('password')
    user = User.query.filter((username == username) &
                             (password == password)).first()
    if not user:
        response = jsonify(
                {"status": 0, "message": "Incorrect username, token or password"})
        return make_response(response)

    result = {"id": user.id, "username": user.username, "fullname": user.fullname,
              "email": user.email, "slug": user.slug, "access_token": user.access_token}
    response = jsonify(
        {"status": 1, "message": "successful login", "user": result})
    return make_response(response)

# Get user by user slug, authentication token required
@app.route('/api/v1/user/<string:slug>', methods=['GET'])
def get_user(slug):
    # Obtain token from Authorization Bearer header
    headers = request.headers
    bearer = headers.get('Authorization')    # Bearer YourTokenHere
    token = bearer.split()[1]  # YourTokenHere
    if not token:
        return make_response(jsonify({"status": 0, "message": "Unauthorised Access"}), 400)
    
    # Query database for user using token and slug
    user = User.query.filter_by(slug=slug).filter_by(access_token=token).first()
    if not user:
        return make_response(jsonify({"status": 0, "message": "Error getting user details"}))

    try:
        # Decode user token
        status_token = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
    # If token is expired, generate a new token and slug to update user
    except jwt.ExpiredSignatureError:
        username = user.username
        newToken = jwt.encode({'username': user.username, 'exp': time.time() + 600}, app.config['SECRET_KEY'], algorithm='HS256')
        user.access_token = newToken
        user.slug = secrets.token_hex(16)
        db.session.commit()
        updated_user = User.query.filter_by(username=username).first()

        result = {"id": updated_user.id, "username": updated_user.username, "fullname": updated_user.fullname, "email": updated_user.email, "slug": updated_user.slug, "access_token": updated_user.access_token}
   
        return make_response(jsonify({"status": 1, "message": "User found", "user": result}))
    # If token is invalid
    except jwt.InvalidSignatureError:
        status_token = False
    
    if not status_token:
        return make_response(jsonify({"status": 0, "message": "Expired or Invalid token"}))

    result = {"id": user.id, "username": user.username, "fullname": user.fullname, "email": user.email, "slug": user.slug, "access_token": user.access_token}
    
    return make_response(jsonify({"status": 1, "message": "User found", "user": result}))

# Get a protected resource
@app.route('/api/v1/resource', methods=['GET'])
def get_resource():
    # Obtain token from Authorization Bearer header
    headers = request.headers
    bearer = headers.get('Authorization')    # Bearer YourTokenHere
    token = bearer.split()[1]  # YourTokenHere
    if not token:
        return make_response(jsonify({"status": 0, "message": "Unauthorised Access"}), 400)

    # Try doecode token
    try:
        jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
    # If token is expired, generate a new token and update user
    except jwt.ExpiredSignatureError:
        user = User.query.filter_by(access_token=token).first()
        if not user:
            return make_response(jsonify({"status": 0, "message": "Error getting resource"}))
        newToken = jwt.encode({'username': user.username, 'exp': time.time() + 600}, app.config['SECRET_KEY'], algorithm='HS256')
        user.access_token = newToken
        user.slug = secrets.token_hex(16)
        db.session.commit()

        return make_response(jsonify({"status": 1, "message": "Expired token updated"}))
    except jwt.InvalidSignatureError:
        return make_response(jsonify({"status": 0, "message": "Expired or Invalid token"}))

    # Everything okay
    # Get resource logic here
    resource = "Everything okay"

    return make_response(jsonify({"status": 1, "message": resource}))

if __name__ == "__main__":
    app.run(debug=True)

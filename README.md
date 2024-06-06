# debate-app-backend
Backend for the Debate App senior capstone project.

**Setup / Installation**

1.) Clone the repo

2.) Create a new file in the main directory and title it ".env"

3.) In the newly created .env file add the following (don't put spaces in between though):

GOOGLE_CLIENT_ID=ClientID

GOOGLE_CLIENT_SECRET=ClientSecret

JWT_SECRET_KEY=Jwtkey

SENDGRID_API_KEY=Sendgridapikey

4.) Replace ClientID, ClientSecret, Jwtkey, Sendgridapikey, with the appropriate values (environment variables).

5.)^^If you want to create new values, you need to register an account for these apis (Google Auth, Sendgrid)

6.) For the JWT key, you can randomly generate a random string of characters/numbers (search up how to do) --> think of like a password.

7.) Don't publicly share your environmental variables

8.) To install the requirements/dependencies run the command: **pip install -r requirements.txt**

**Running on localhost**

**1.) Locate these lines near the top of main.py (39-41 in this commit):**

39 #CORS(app, origins=['http://localhost:3000'], supports_credentials=True) #<--disable on deploy

40 # # CORS(app, origins=['http://localhost:3000', 'https://test-debate-frontend-update-deploy.onrender.com', 'https://debate-app-backend.onrender.com'], supports_credentials=True)

41 CORS(app, resources={r"/*": {"origins": "https://www.rohanjoshi.dev", "supports_credentials": True}}) #<--enable on deploy

Comment out line 41 (or whichever line the code above corresponds to) and uncomment line 39. 

**2.) Locate these lines (55-56 in this commit):**

55 app.config["JWT_COOKIE_SAMESITE"] = "None"

56 app.config["JWT_COOKIE_DOMAIN"] = "rohanjoshi.dev"

Comment these lines out.

**3.) Locate these lines (393-394 in this commit):**

393 #response.set_cookie('access_token_cookie', value=jwt_token, secure=True)

394

395 response.set_cookie('access_token_cookie', value=jwt_token, secure=True, httponly=True, samesite='None', domain="rohanjoshi.dev")

Uncomment line 393. Comment out line 395.

**4.) Running on Windows/Mac can differ (search how to run flask apps on Mac for example), but generally you can do: **

**5.) Enter command: set FLASK_APP=main.py**

**6.) Enter command: $env:FLASK_APP="main.py"**

**7.) Enter command: flask run**

**Adding Initial User/Admin**

1.) Go to main.py

2.) Ctrl+f or find the function: create_initial_user()

3.) Replace the emails/name with your own information.

4.) Re-run the flask




Technologies used in the backend:
- Python / Flask
- SQLite
- Sendgrid

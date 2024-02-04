# Travimate Authentication Backend using Node + Express JS
## How to install
1. clone this repository into your chosen folder
   `git clone https://github.com/Travimate/Backend-Auth.git`
2. Install postgresql (psql) (if you haven't)
3. Inside local cloned repository, open terminal and run these codes
   `npm install` and then 
   `npm update`
4. Open api/config/db.config.js, adjust the database configuration (HOST, USER, PASSWORD, and DB Name) as your preferences. after set the DB, you need to create Database in psql with the same name as your preferences
5. Copy file `env.example`, and rename into `.env`, and adjust the key value (EMAIL_SENDER, EMAIL_PASSWORD or your google (app password)[https://support.google.com/accounts/answer/185833?hl=en], and BASE_URL like http://localhost:8080) 
6. In terminal run `npm start` to run the backend API server, use postman to test endpoints
7. In your browser, open `{your_base_url}/api-docs`, like http://localhost:8080/api-docs, to open Swagger API Documentation
8. Done!

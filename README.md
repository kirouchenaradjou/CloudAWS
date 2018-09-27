# csye6225-fall2018

# Team Member Information:
- Akilan Rajendiran         rajendiran.a@husky.neu.edu
- Menita Koonani            koonani.m@husky.neu.edu
- Neha Pednekar             pednekar.n@husky.neu.edu
- Raghavi Kirouchenaradjou  kirouchenaradjou.r@husky.neu.edu 



# Prerequisites for build and deployment of the web app

- Should have installed IntelliJ IDEA and MySQL server to run the app locally
- JUnit must be installed for Unit testing of the code
- Should have Advanced Rest client/POSTMAN installed as a Chrome extension

# Instructions to build and deploy the web app

- App must be imported in the IntelliJ and MySQL server must be configured to run the app.
- The application.properties should be set on your local to match your MySQL password for the root user.
- To run the Spring Boot application, click on the green run button on the top panel.

# How to run the APi
# Run it via either Postman or AdvancedRestClient 
Following are the 2 API's been supported :
http://localhost:8080/user/register
method : post 
{"userName" : "user1@gmail.com",
"password" : "user"}

Response :

{
"message": "Registration Successful"
}


http://localhost:8080/user/register
method : post 
{"userName" : "user1@gmail.com",
"password" : "user"}

Response :

{
"message": "User already exists!"
}

http://localhost:8080/time
authorization : Basic cmFnaGF2aXVzZXJAZ21haWwuY29tOnJhZ2hhdmk= (valid user)
method : get

Response : 
{
"message": "Current Time is : Thu Sep 27 16:06:41 EDT 2018"
}

http://localhost:8080/time
authorization : Basic invalid= (invalid user)
method : get

Response : 
{
"message": "User not found! - Try Logging in again"
}

http://localhost:8080/time
no autorzation header

{
"message": "You are not logged in!"
}


# Instructions to run Unit Test, Integration and Load tests

- To perform the Unit Testing of the modules and controller, run the JUnit tests.
- Need to validate the input via a payload along with username and password using the Advanced Rest Client/POSTMAN app.
- Need to validate the GET and POST requests with relevant JSON payloads

# Link to TravisCI build

  https://travis-ci.com/AkilanRajendiran/csye6225-fall2018






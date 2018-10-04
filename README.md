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


#Transaction API

http://localhost:8080/transaction
authorization : Basic dXNlcjFAZ21haWwuY29tOnVzZXI= (valid user)
method : post
status code: 200 Created
payload: {
  "id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "description": "coffee",
  "merchant": "starbucks",
  "amount": 2.69,
  "date": "09/25/2018",
  "category": "food"
}

Response : 
{
    "message": "Transaction  Successful"
}

http://localhost:8080/transaction
authorization : No Auth
method : post
status code: 401(Unauthorized)
payload: {
  "id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "description": "coffee",
  "merchant": "starbucks",
  "amount": 2.69,
  "date": "09/25/2018",
  "category": "food"
}

Response : 
{
    "message": "You are not logged in - Provide Username and Password!"
}

http://localhost:8080/transaction
authorization : Basic Auth(valid user)
method: post
payload: Incorrect Payload
statuc code: 400(Bad Request)

Response:
{
    "timestamp": "2018-10-04T20:07:28.968+0000",
    "status": 400,
    "error": "Bad Request",
    "message": "Required request body is missing: public org.springframework.http.ResponseEntity com.csye6255.web.application.fall2018.controller.TransactionController.createTransactions(javax.servlet.http.HttpServletRequest,com.csye6255.web.application.fall2018.pojo.Transaction)",
    "path": "/transaction"
}

http://localhost:8080/transaction
authorization : No Auth
method : get
status code: 401(Unauthorized)

Response : 
{
    "message": "You are not logged in!"
}

http://localhost:8080/transaction
authorization : Basic dXNlcjFAZ21haWwuY29tOnVzZXI= (valid user)
method : get
status code: 200 OK

Response : 
[
    {
        "id": "a943b525-6d7e-4df0-85f9-169cb09a5795",
        "description": "coffee",
        "merchant": "2.69",
        "amount": "09/25/2018",
        "date": "starbucks",
        "category": "food"
    },
    {
        "id": "c411c9c5-7a2e-44df-9871-bb473bb7bcd0",
        "description": "coffee",
        "merchant": "2.69",
        "amount": "09/25/2018",
        "date": "starbucks",
        "category": "food"
    }
]

http://localhost:8080/transaction/{transactionid}
authorization : Basic dXNlcjFAZ21haWwuY29tOnVzZccdsdsfXI= (invalid user)
method : put
status code: 401(Unauthorized)

Response : 
{
    "message": "User not found! - Try Logging in again"
}

http://localhost:8080/transaction/{transactionid} {where transactionid = "d290f1ee-6c54-4b01-90e6-d701748f0851"}
authorization : Basic dXNlcjFAZ21haWwuY29tOnVzZXI= (valid user)
method : put
status code: 201 Created
payload: {
  "id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "description": "tea",
  "merchant": "kungfu",
  "amount": 2.69,
  "date": "09/25/2018",
  "category": "food"
}

Response (updated payload): 
{
    "id": "a943b525-6d7e-4df0-85f9-169cb09a5795",
    "description": "tea",
    "merchant": "2.69",
    "amount": "09/25/2018",
    "date": "kungfu",
    "category": "food"
}

http://localhost:8080/transaction/{transactionid} {where trsnactionID = Invalid}
authorization : Basic dXNlcjFAZ21haWwuY29tOnVzZXI= (valid user)
method : put
status code: 400 bad request
payload: {
  "id": "d290f1ee-6c54-4b01-90e6-999999999999",
  "description": "coffee",
  "merchant": "starbucks",
  "amount": 2.69,
  "date": "09/25/2018",
  "category": "food"
}

Response:
{
    "timestamp": "2018-10-04T20:07:28.968+0000",
    "status": 400,
    "error": "Bad Request",
    "message": "Required request body is missing: public org.springframework.http.ResponseEntity com.csye6255.web.application.fall2018.controller.TransactionController.createTransactions(javax.servlet.http.HttpServletRequest,com.csye6255.web.application.fall2018.pojo.Transaction)",
    "path": "/transaction"
}

http://localhost:8080/transaction/{transactionid} 
authorization : No Auth
method: put
payload: correct Payload
status code: 401 Unauthorized

http://localhost:8080/transaction/{transactionid} (where transactionid is valid)
authorization : Basic dXNlcjFAZ21haWwuY29tOnVzZXI= (valid user)
method: delete
status code: 204 No Content

http://localhost:8080/transaction/{transactionid} (where transactionid is invalid)
authorization : Basic dXNlcjFAZ21haWwuY29tOnVzZXI= (valid user)
method: delete
status code: 400 Bad Request

http://localhost:8080/transaction/{transactionid} (where transactionid is invalid)
authorization : No Auth
method: delete
status code: 401 Unauthorized


# Instructions to run Unit Test, Integration and Load tests

- To perform the Unit Testing of the modules and controller, run the JUnit tests.
- Need to validate the input via a payload along with username and password using the Advanced Rest Client/POSTMAN app.
- Need to validate the GET and POST requests with relevant JSON payloads

# Link to TravisCI build

  https://travis-ci.com/AkilanRajendiran/csye6225-fall2018


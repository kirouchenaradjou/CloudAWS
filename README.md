# csye6225-fall2018
# Team Members:
- Akilan Rajendiran         rajendiran.a@husky.neu.edu
- Menita Koonani            koonani.m@husky.neu.edu
- Neha Pednekar             pednekar.m@husky.neu.edu
- Raghavi Kirouchenaradjou  kirouchenaradjou.r@husky.neu.edu 
# csye6225-fall2018
# Team Members:
- Akilan Rajendiran         rajendiran.a@husky.neu.edu
- Menita Koonani            koonani.m@husky.neu.edu
- Neha Pednekar             pednekar.m@husky.neu.edu
- Raghavi Kirouchenaradjou  kirouchenaradjou.r@husky.neu.edu 

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


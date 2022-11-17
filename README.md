# transaction-service

This is a quick implementation of the requirements described in the email.

I have not added any testing or deployed to a cloud simply because of time constraints.

We can discuss in depth optimizations and different approaches that can be taken, 
as there are plenty of options on how to read/write to files and optimize searching.

This application is being deployed on <b>http://localhost:8080<b>

Example requests:

POST  : http://localhost:8080/transaction - Request body as JSON 
  
GET   : http://localhost:8080/transaction?date=11-12-2018&type=credit

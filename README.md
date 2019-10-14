## Money transfer Rest API
Java RESTful API for transferring money from one account to another.

### Technologies

* Spark Java (Light weight microservices backend framework with embedded Jetty container).
* Guice for dependency injection.
* Swagger for RESTful API documentation.
* Mockito (for mocking calls in unit testing).
* Junit for testing the API.
* Use in-memory data store (Concurrent HashMap).

### How to Run the Application

mvn exec:java

### Testing
TDD practice has been followed in developing the API in following areas
* Unit Testing
* Integration Testing

### Features 
* Create new account with <i>unique</i> userId, initial balance and currency.
* Deposit money in the account.
* Withdraw money from the account.
* Transfer money between two accounts.
* Delete the account.
* Get account detail for requested account ID.
* Get all accounts detail exist in data store. 
          

### API Usage

HTTP METHOD | PATH | USAGE
--- | --- | ---
POST | /account | Create a new account
GET | /account/:accountId | Get account detail for requested account ID
GET | /accounts | Get all accounts detail exist in data store
DELETE | /account/:accountId | Delete account for requested account ID
DELETE | /accounts | Delete all accounts exist in data store
PUT | /account/withdraw | Withdraw money from account provided in request body
PUT | /account/deposit | Deposit money in account provided in request body
POST | /account/transfer | Transfer money from one account to another account

#### Sample request JSON for account related operations

##### Create Account
    Request:
        {
         "userId": "foo",
         "balance": "2000",
         "currency": "GBP"
        }
    
    Response:
        {
         "status": "SUCCESS",
         "message": "New account has been created",
         "data": "d1f40220-3aea-44b8-91f0-930faef6df3c"
        }
                
##### Deposit money
    Request:
        {
         "accountId" : "d1f40220-3aea-44b8-91f0-930faef6df3c",
         "amount" : "200"
        }        
    Response:
        {
         "status": "SUCCESS",
         "message": "Amount has been deposit to account: d1f40220-3aea-44b8-91f0-930faef6df3c"
        }   
        
##### Withdraw money
    Request:
        {
         "accountId" : "d1f40220-3aea-44b8-91f0-930faef6df3c",
         "amount" : "120"
        }       
    Response:
        {
         "status": "SUCCESS",
         "message": "Amount has been withdrawn from account: d1f40220-3aea-44b8-91f0-930faef6df3c"
        }  
        
##### Transfer money        
    Request:
        {
         "fromAccount" : "d1f40220-3aea-44b8-91f0-930faef6df3c",
         "toAccount" : "d2bc4a1f-3c7f-4644-bfa0-00ede9b81114",
         "amount" : "130"
        }
    Response:    
        {
         "status": "SUCCESS",
         "message": "Money has been transferred successfully"
        }
        
### Https Status
* 200 OK: The request has succeeded
* 400 Bad Request: The request could not be understood by the server
* 404 Not Found: The requested resource cannot be found
* 409 Conflict: The request conflict with current state of the server
* 500 Internal Server Error: The server encountered an unexpected condition 


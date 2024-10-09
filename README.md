For all queries to the server, a token must be sent in the header,
except for registration and login
(Token is valid for 2hours)
example:
headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  }

#1
Registration Request:
http://localhost:9191/auth/register
Exmaple Request:
{
    "firstname": "example",
    "lastname": "example",
    "email": "example@gmail.com",
    "password": "example",
    "pin": 12345, -> server allows the String but preferred value is int
    "phoneNumber": 123456789
}
Example Response:
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJza2lrYWMzQGdtYWlsLmNvbSIsImlhdCI6MTcyODMyMDIyNiwiZXhwIjoxNzI4MzIzODI2fQ.xLKBWm2pJ16L2al4swSRkXaqOsyEZlH3E1wS7Vjac-s"
}
Example Exception:
{
    "timeStamp": "2024-10-07T17:35:37.321+00:00",
    "isError": true,
    "series": "CLIENT_ERROR",
    "isSuccessfull": false,
    "message": "Email already exists",
    "status": "BAD_REQUEST",
    "statusCode": 400
}



#2
Authentication Request:
http://localhost:9191/auth/authenticate
Example Request:
{
    "email": "example@gmail.com",
    "password": "example"
}
Example Response:
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJza2lrYWMzQGdtYWlsLmNvbSIsImlhdCI6MTcyODMyMDMxNSwiZXhwIjoxNzI4MzIzOTE1fQ.skdwVy-H845S8MVlEmgzXcODNAqpMIeRR_Msn1UqdQg"
}
Example Exception:
{
    "timeStamp": "2024-10-07T16:58:14.395+00:00",
    "isError": true,
    "series": "SERVER_ERROR",
    "isSuccessfull": false,
    "message": "Authentication failed: Bad credentials",
    "status": "INTERNAL_SERVER_ERROR",
    "statusCode": 500
}



#3
Transfer Money Request:
http://localhost:9191/transferMoney
Example Request:
{
    "receiverEmail": "example@gmail.com",
    "balanceToSend": 200,
    "pin": 12345,
    "transactionType": "example",  -> Can be customised by User
    "description": "debt"  -> Can be customised by User
}
Example Response:
Funds has been sent.
Example Exception:
{
    "timeStamp": "2024-10-07T17:38:16.248+00:00",
    "isError": true,
    "series": "CLIENT_ERROR",
    "cause": "N/A",
    "isSuccessfull": false,
    "message": "Receiver not found with email: skikac45@gmail.com",
    "status": "NOT_FOUND",
    "statusCode": 404
}



#4
Transactions for user request:
http://localhost:9191/transaction/user
Example Request:
It requires only token in 'Authorization' in header
Example Response:
[
    {
        "dateOfCreation": "2024-10-07T17:42:09.987+00:00",
        "amount": 200.0,
        "transactionType": "common funds transfer",
        "description": "debt",
        "receiverEmail": "example4@gmail.com",
        "receiverName": "example",
        "senderEmail": "example@gmail.com",
        "senderName": "example"
    },
    {
        "dateOfCreation": "2024-10-07T17:43:20.724+00:00",
        "amount": 300.0,
        "transactionType": "common funds transfer",
        "description": "debt",
        "receiverEmail": "example@gmail.com",
        "receiverName": "example",
        "senderEmail": "example@gmail.com",
        "senderName": "example"
    }
]
Example Exception:
It doesn't throw exception on client side but set a status Code



#5
Generate Blik Code Request:
http://localhost:9191/generate/blikcode
Example Request:
It requires only token in 'Authorization' in header
Example Response:
{
    "blikCode": "580526",
    "creatorEmail": "example@gmail.com",
    "expirationTime": "2024-10-07T18:27:40.190+00:00"
}
Example Exception:
It doesn't throw exception on client side but set a status Code



#6
Generate Blik Request:
http://localhost:9191/generate/requestBlik
Example Request:
It requires 2 Parameters: blikCode, requestedFunds
After sending 2 parameters url should looks for example: http://localhost:9191/generate/requestBlik?blikCode=580526&requestedFunds=200
Example Response:
{
    "blikCode": "535798",
    "requesterEmail": "skikac4@gmail.com",
    "requestedFunds": 200.0,
    "status": "PENDING",
    "message": "BLIK request is created and waiting for owner acceptance"
}
Example Exception:
Blik code not found

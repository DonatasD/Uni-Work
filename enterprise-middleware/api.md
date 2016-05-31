Flight booking API
===================
Author: Donatas Daubaras

This is the API for flight booking service, which supports various RESTFul end points which also includes JSON supprt for cross domain requests.

Default base URI(domain) for service is: `jbosscontactsangularjs-110336260.rhcloud.com`.

CustomerService End Points
----------------------------
##CREATE
### Create a new Customer

#### /rest/customers

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```Javascript
{"name":"Don","email":"donatas@gmail.com","phoneNumber":"07591440518"}
```

* Response example:
* Success: 201 CREATED
* Validation error: Collection of `<field name>:<error msg>` for each error

```JavaScript
{"email":"That email is already used, please use a unique email"}
```

##READ
### List all customers
#### /rest/customers

* Request type: GET
* Return type: JSON
* Response example:
* Success: 200 OK
```JavasScript
[
    {
        "id": 1,
        "name": "Don",
        "email": "donatas@gmail.com",
        "phoneNumber": "07591440518"
    },
    {
        "id": 6,
        "name": "Don",
        "email": "dsdatas@gmail.com",
        "phoneNumber": "07591440518"
    }
]
```

### Find a customer by it's ID.
#### /rest/customers/\<id>
* Request type: GET
* Return type: JSON
* Response example:

```JavaScript
{
    "id": 1,
    "name": "Don",
    "email": "donatas@gmail.com",
    "phoneNumber": "07591440518"
}
```

### Find a customer by it's email.
#### /rest/customers/\<email>
* Request type: GET
* Return type: JSON
* Response example:

```JavaScript
{
    "id": 1,
    "name": "Don",
    "email": "donatas@gmail.com",
    "phoneNumber": "07591440518"
}
```

##UPDATE
### Edit one customer
#### /rest/customers/\<id>

* Request type: PUT
* Return type: JSON
* Response example:

```JavaScript
{
    "id": 1,
    "name": "Awesome guy",
    "email": "donatas@gmail.com",
    "phoneNumber": "07591440518"
}
```

FlightService End Points
-------------------------

##CREATE
### Create a new flight
#### /rest/flights

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```JavaScript
{
    "id": 7,
    "flightNumber": "ABC12",
    "departurePoint": "NCL",
    "destinationPoint": "VLN"
}
```
* Response example:
* Success: 200 OK
* Validation error: Collection of `<field name>:<error msg>` for each error

```JavaScript
{
    "flightNumber": "This flight number is already used, please use a unique flight number"
}
```

##READ
### List all flights
#### /rest/flights

* Request type: GET
* Return type: JSON
* Response example:

```Javascript
[
    {
        "id": 7,
        "flightNumber": "ABC12",
        "departurePoint": "NCL",
        "destinationPoint": "VLN"
    },
    {
        "id": 2,
        "flightNumber": "ABC32",
        "departurePoint": "NCL",
        "destinationPoint": "VLN"
    }
]
```

### Find a flight by it's ID.
#### /rest/flights/id/\<id>

* Request type: GET
* Return type: JSON
* Response example:

```Javascript
{
    "id": 2,
    "flightNumber": "ABC32",
    "departurePoint": "NCL",
    "destinationPoint": "VLN"
}
```

### Find a flight by it's flight number
#### /rest/flights/flightnumber/\<flighNumber>

* Request type: GET
* Return type: JSON
* Response example:

```Javascript
{
    "id": 2,
    "flightNumber": "ABC32",
    "departurePoint": "NCL",
    "destinationPoint": "VLN"
}
```

##UPDATE
### Edit one flight
#### /rest/flights/\<id>

* Request type: PUT
* Return type: JSON
* Response example:

```Javascript
{
    "id": 1,
    "flightNumber": "ABC32",
    "departurePoint": "NCL",
    "destinationPoint": "VLN"
}
```

BookingService End Points
--------------------------
##CREATE
### create a new booking
#### /rest/bookings

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```Javascript
{
  "flightId": 2,
  "customerId" : 3,
  "bookingDate" : "2014-12-10"
}
```

* Response example:
* Success: 200 OK
* Validation error: Collection of `<field name>:<error msg>` for each error

```Javascript
{
    "bookingDate": "Booking date must be in the future"
}
```

##READ
### List all bookings
#### /rest/bookings

* Request type: GET
* Return type: JSON
* Response example:

```Javascript
[
    {
        "id": 4,
        "customerId": 3,
        "flightId": 2,
        "bookingDate": "2014-12-10"
    },
    {
        "id": 5,
        "customerId": 3,
        "flightId": 2,
        "bookingDate": "2014-12-11"
    }
]
```

##DELETE
### Delete a single Booking by it's ID
#### /rest/bookings/\<id>

* Request type: DELETE
* Return type: JSON
* Response example:

```Javascript
{
    "id": 3,
    "customerId": 1,
    "flightId": 2,
    "bookingDate": "2014-12-10"
}
```

##POST
### Create a Travel Agency Booking
#### /rest/agency

* Request type: POST
* Return type: JSON
* Response example

```Javascript
{
    "id": 2,
    "customerEmail": "d.daubaras@ncl.ac.uk",
    "flightId": 10001,
    "hotelId": 1027,
    "taxiId": 100,
    "flightBookingId": 9,
    "hotelBookingId": 1,
    "taxiBookingId": 7,
    "flightBookingDate": "2017-12-11",
    "hotelBookingDate": "2016-12-11",
    "taxiBookingDate": "2016-12-11"
}
```

* Response example:
* Success: 200 OK
* Validation error: Collection of `<field name>:<error msg>` for each error

```Javascript
{
    "error": "failed to create bookings"
}
```

##GET
### Retrieve all Travel Agency Bookings
#### /rest/agency

```Javascript
[
    {
        "id": 2,
        "customerEmail": "d.daubaras@ncl.ac.uk",
        "flightId": 10001,
        "hotelId": 1027,
        "taxiId": 100,
        "flightBookingId": 9,
        "hotelBookingId": 1,
        "taxiBookingId": 7,
        "flightBookingDate": "2017-12-11",
        "hotelBookingDate": "2016-12-11",
        "taxiBookingDate": "2016-12-11"
    },
    {
        "id": 3,
        "customerEmail": "d.daubaras@ncl.ac.uk",
        "flightId": 10001,
        "hotelId": 1027,
        "taxiId": 100,
        "flightBookingId": 10,
        "hotelBookingId": 2,
        "taxiBookingId": 8,
        "flightBookingDate": "2020-12-11",
        "hotelBookingDate": "2020-12-11",
        "taxiBookingDate": "2020-12-11"
    }
]
```

##DELETE
### Delete a specific travel agency booking by specified id in the path
#### /rest/agency/<id>

* Response example:
* Success: 204 No Content
* Validation error: Collection of `<field name>:<error msg>` for each error

```Javascript
{
    "error": null
}
```

---------------------------------------------

Author: Hugo Firth

ContactService End Points
------------------------
##CREATE
### Create a new contact

#### /rest/contacts

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```JavaScript
{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "223-223-1231", birthDate:'1966-01-03'}
```

* Response example:
* Success: 200 OK
* Validation error: Collection of `<field name>:<error msg>` for each error

```JavaScript
{"email":"That email is already used, please use a unique email"}
```

##READ
### List all contacts
#### /rest/contacts

* Request type: GET
* Return type: JSON
* Response example:

```javascript
[{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "223-223-1231", birthDate:'1966-01-03'},
 {email: "john.doe@company.com", id: 15, firstName: "John", lastName: 'Doe', phoneNumber: "212-555-1212", birthDate:'1978-02-23'}]
```

### Find a contact by it's ID.
#### /rest/contacts/\<id>
* Request type: GET
* Return type: JSON
* Response example:

```javascript
{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "223-223-1231", birthDate:'1966-01-03'}
```


##UPDATE
### Edit one contact
#### /rest/contacts

* Request type: PUT
* Return type: JSON
* Response example:

```javascript
{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "223-223-1231", birthDate:'1966-01-03'}
```


##DELETE
### Delete one contact
#### /rest/contacts

* Request type: DELETE
* Return type: JSON
* Response example:

```javascript
{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "223-223-1231", birthDate:'1966-01-03'}
```


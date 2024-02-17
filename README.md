# Backend Java Travimate


Aplikasi travimate menggunakan 2 aplikasi backend. Ini adalah dokumentasi untuk Backend Java. Beberapa environment dan tools yang digunakan untuk membangun aplikasi ini antara lain:

- Spring Boot (Framework)
- Maven (Build Tools)
- Github (Version Control & CI/CD)
- PostgreSQL (Database)
- PGAdmin4 (Web Database Administrator)
- Postman (API Testing)
- Intellij IDEA (Text Editor)

**Klik disini untuk melihat [Postman Documentation](https://documenter.getpostman.com/view/30926635/2sA2r813zs)**

## Author

>- Akhmad Fajar Firman Syah (BEJ2) => Java Apps - Main Feature (Flight, Order, Payment, Report)  
>- Aditya Bagus (FSW) => NodeJS Apps - Authentication & User Managemnt 

## Auth
Authentication ini di bangun menggunakan aplikasi NodeJS, digunakan untuk menghasilkan token JWT. Token ini akan digunakan sebagai Authorization Header untuk mengakses API yang ada di aplikasi JAVA

### POST signin

#### Request Body
```JSON
{
    "email": "user@gmail.com",
    "password": "$User123"
}
```
#### Response
```JSON
{
    "status": 200,
    "username": "user",
    "email": "user@gmail.com",
    "dob": "2024-01-16T00:00:00.000Z",
    "phone": "85123455566",
    "greeting": "mr",
    "pp": "https://res.cloudinary.com/dmta1mm4p/image/upload/v1707805375/slbzq9etce6o00r2mkw4.png",
    "roles": [
        "ROLE_USER",
        "ROLE_ADMIN"
    ],
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIiwiUk9MRV9BRE1JTiJdLCJpYXQiOjE3MDgxNzgwNzIsImV4cCI6MTcwODE4NjQ3Mn0.4jnhu2uJlvG1muakpV3rhdxrkbC5kiLxaFF-i6HqpYQ",
    "emailVerified": false,
    "emailVerificationToken": "471b406f-aade-42dc-b833-a6f3ef8e1f0d"
}
```

## Flight
Di dalam flight controller terdapat beberapa fitur diantaranya:

> Secured for User, accessable for Admin 
  

- (POST) /flight  
    flight merupakan data penerbangan dari satu bandara ke bandara yang lainnya. terdiri dari informasi satu penerbangan seperti nomer penerbangan, maskapai, stok tiket, bandara kebrangkatan dan kedatangan, waktu tempuh, tanggal keberangkatan.  
    \*dapat menambahkan banyak data flight dalam sekali request
- (POST) /flight-data  
    flight-data merupakan fitur untuk membuat rute penerbangan. terdiri dari beberapa informasi rute seperti harga, stops, waktu tempuh, berapa penerbangan dalam suatu journey (transit flight) dan informasi lainnya dapat ditambahkan disini.  
    \*dapat menambahkan banyak flight-data dalam sekali request
- (POST) /edit-airline  
    digunakan untuk mengubah icon url dari setiap maskapai  
    \*dapat menambahkan banyak data url dalam sekali request
    

> No secured. Open for public 
  

- (GET) /find-journey  
    merupakan fitur pencarian penerbangan
    

> Untuk menghemat ruang server, maka akan dilakukan penghapusan untuk setiap data flight di hari kemaren yang tidak terikat dengan entitas Orders dan Passengers. Penghapusan data ini menggunakan anotasi scheduled yang disediakan spring framework dengan cron job setiap hari pada jam 00.00 
  

<img src="https://content.pstmn.io/7cca0c96-b97b-4c3c-a9ca-b0e78bfe5380/aW1hZ2UucG5n">

> Untuk setiap pencarian penerbangan dengan tanggal sebelum hari ini, maka akan mengebalikan data kosong. Karena penerbangan kemarin sudah tidak mungkin untuk dipesan hari ini 
  

<img src="https://content.pstmn.io/588a90bc-155b-4b57-95eb-f6ae862b8bab/aW1hZ2UucG5n">


### POST /flight

#### Request Body Example
```JSON
[
    {
    "dep": "CGK",
    "arr": "DPS",
    "airline": "JT",
    "flightNumber": "232",
    "flightClass": "ECONOMY",
    "dof": "2024-02-01",
    "depTime": "06.00",
    "arrTime": "08.55",
    "stock": 150
  },
  {
    "dep": "CGK",
    "arr": "DPS",
    "airline": "QZ",
    "flightNumber": "814",
    "flightClass": "ECONOMY",
    "dof": "2024-02-01",
    "depTime": "21.30",
    "arrTime": "00.20",
    "stock": 150
  }
]
```

### POST /flight-data

#### Request Body Example
```JSON
[
  {
    "airline": "GA",
    "dep": "CGK",
    "arr": "DPS",
    "adultFare": "1239000",
    "sameAsAdult": true,
    "flightClass": "ECONOMY",
    "connectingAirport": "SUB",
    "isDirect": false,
    "date": "2024-02-01"
  },
  {
    "airline": "JT",
    "dep": "CGK",
    "arr": "DPS",
    "adultFare": "920000",
    "sameAsAdult": true,
    "flightClass": "ECONOMY",
    "connectingAirport": null,
    "isDirect": true,
    "date": "2024-02-01"
  }
]
```

### POST /edit-airline

#### Request Body Example

```JSON
[
  {
    "iataCode": "QG",
    "url": "https://ik.imagekit.io/tvlk/image/imageResource/2015/12/17/1450350561012-6584b693edd67d75cfc25ecff41c5704.png?tr=q-75"
  },
  {
    "iataCode": "SJ",
    "url": "https://ik.imagekit.io/tvlk/image/imageResource/2019/12/12/1576139484141-e3bb88cae8d9f1f89d9b3d1b8788c450.png?tr=q-75"
  }
]
```

### GET /find-journey

#### Request Param Example
Query Params
- dep : cgk
- arr : dps
- dateDep : 2024-02-01
- flightClass : economy
- isAroundTrip : false
- dateArr : 2024-01-04

```HTTP
http://34.124.247.20:8080/api/v1/flight/find-journey?dep=cgk&arr=dps&dateDep=2024-02-01&flightClass=economy&isAroundTrip=false
```

## Order
Fitur yang terdapat di order hanya dapat diakses oleh authorized user. Disini user dapat menambahkan pesanan tiket penerbangan, membatalkan pesanan, dan melihat riwayat pesanan

> Authorization : Bearer Token

### POST /order/add

#### Request Body Example
```JSON
{
  "bookedBy": "fajar",
  "bookedMail": "fa314270@gmail.com",
  "flightDataID": "7295cebf-047e-4904-974f-f42e5135b248",
  "flightID": [
    "8767bed7-69ec-48fd-9d38-54b5bb7764d1"],
  "passengerList": [
    {
      "greeting": "Mrs.",
      "firstName": "Syafa",
      "lastName": "Annisa",
      "type": "adult"
    },
    {
      "greeting": "Miss",
      "firstName": "Sheilla",
      "lastName": "Amira",
      "type": "child"
    },
    {
      "greeting": "Miss",
      "firstName": "Syahnaz",
      "lastName": "Olivia",
      "type": "child"
    }
  ]
}

```

### POST /order/cancel

#### Request Param Example
Query Params
- orderID : 757d87c9-6e7f-4072-a03d-01e0125daa63

```HTML
http://34.124.247.20:8080/api/v1/order/cancel?orderID=757d87c9-6e7f-4072-a03d-01e0125daa63
```

### GET /order/history

#### Request Example
```HTML
http://34.124.247.20:8080/api/v1/order/history
```
#### Response
```JSON
{
    "data": [
        {
            "orderID": "efe94253-842b-42d5-b6b7-7e524b8d1216",
            "username": "user",
            "bookingID": "TVB4PQ8X",
            "bookedBy": "fajar",
            "bookedDate": [
                2024,
                2,
                16
            ],
            "bookedMail": "fa314270@gmail.com",
            "pnrCode": "9445913",
            "amount": 3060000,
            "flightList": [
                {
                    "flightID": "8767bed7-69ec-48fd-9d38-54b5bb7764d1",
                    "flightNumber": "QZ814",
                    "dep": {
                        "iata_code": "CGK",
                        "airport_name": "Soekarno-Hatta International Airport",
                        "city": "Jakarta",
                        "country": "Indonesia"
                    },
                    "arr": {
                        "iata_code": "DPS",
                        "airport_name": "Ngurah Rai (Bali) International Airport",
                        "city": "Denpasar",
                        "country": "Indonesia"
                    },
                    "airline": {
                        "iata_code": "QZ",
                        "airline_name": "AirAsia (Indonesia)",
                        "imageUrl": "https://ik.imagekit.io/tvlk/image/imageResource/2022/09/05/1662367239331-9fca504de7049b772dd2386631705024.png?tr=q-75",
                        "cabinBaggage": 7,
                        "baggage": 20
                    },
                    "flightClass": "ECONOMY",
                    "dof": [
                        2024,
                        2,
                        1
                    ],
                    "departure_time": [
                        21,
                        30
                    ],
                    "arrival_time": [
                        0,
                        20
                    ],
                    "flight_time": [
                        2,
                        50
                    ],
                    "stock": 150
                }
            ],
            "passengerList": [
                {
                    "id": "cab6f950-5433-4989-b942-4f3a33616171",
                    "greeting": "Mrs.",
                    "firstName": "Syafa",
                    "lastName": "Annisa",
                    "ticketId": "WNIPG9530378",
                    "flightNumber": "QZ814"
                },
                {
                    "id": "67f3e1b9-9444-49b3-93c7-d5db4079d61f",
                    "greeting": "Miss",
                    "firstName": "Sheilla",
                    "lastName": "Amira",
                    "ticketId": "WNIPG9722358",
                    "flightNumber": "QZ814"
                },
                {
                    "id": "26440fca-6586-4ef8-b2d0-ac8875581bc2",
                    "greeting": "Miss",
                    "firstName": "Syahnaz",
                    "lastName": "Olivia",
                    "ticketId": "WNIPG0702804",
                    "flightNumber": "QZ814"
                }
            ],
            "completed": null,
            "paid": false
        }
    ],
    "message": "success",
    "status": 200
}
```

## Payment

Metode pembayaran ini belum terikat dengan gateway pembayaran manapun. Hanya sebatas data dummy untuk memberi label pesanan apakah sudah terbayar atau belum.

Di dalam proses pembayaran ini terdapat anotasi _Transactional_ dari _spring framework_ untuk memastikan bahwa jika ada kesalahan dalam menjalankan proses pembayaran maka stok tiket di dalam entitas _**Flight**_ tidak akan berkurang

<img src="https://content.pstmn.io/b43d0254-9f96-4745-8f3b-f37c77d75bef/aW1hZ2UucG5n">

### POST /payment/pay

#### Request Param
Query Params
- orderID : efe94253-842b-42d5-b6b7-7e524b8d1216
- method : bank
- isPaid : false

Metode Pembayaran :
- "ewallet"
- "bank"

```HTML
http://34.124.247.20:8080/api/v1/payment/pay?orderID=efe94253-842b-42d5-b6b7-7e524b8d1216&method=bank&isPaid=false
```
### GET /payment/status

#### Request Param
Query Params
- paymentID : 4da2cf22-4e72-4df9-b6ac-1e7a428a275e

```HTML
http://34.124.247.20:8080/api/v1/payment/status?paymentID=4da2cf22-4e72-4df9-b6ac-1e7a428a275e
```

## Payment

Fitur ini digunakan untuk membuat file pdf ataupun xml untuk e-ticket dan laporan penjualan maskapai

<img src="https://content.pstmn.io/66b01466-3865-4fee-ba0a-899addb755d0/U2NyZWVuc2hvdCAoMTQ3KS5wbmc=" alt="airline-revenue">

<img src="https://content.pstmn.io/b1763834-6f50-4f2f-b621-66151a676919/U2NyZWVuc2hvdCAoMTQyKS5wbmc=" alt="e-ticket">

> Untuk pembuatan laporan penjualan maskapai terdapat 4 periode yang harus dimasukkan ke dalam parameter tipe data _String_ yaitu : 
> - "yearly" 
> - "monthly"
> - "weekly"
> - "custom" 

dengan pettern "yyyy-MM-dd" untuk _weekly_ dan _custom_

### GET /report/airline-revenue

#### Request Param
Query Params

- iataCode : QZ
- format : pdf
- periode : yearly
- (optional) month : 02
- (optional) year : 2024
- (optional) week : 01
- (optional) startDate : 2024-01-20
- (optional) endDate : 2024-02-20

```HTML
http://34.124.247.20:8080/api/v1/report/airline-revenue?iataCode=QZ&format=pdf&periode=yearly&year=2024
```

#### Result
<img src="https://content.pstmn.io/66b01466-3865-4fee-ba0a-899addb755d0/U2NyZWVuc2hvdCAoMTQ3KS5wbmc=" alt="airline-revenue">

### GET /report/e-ticket

#### Request Param
Query Params

- orderID : efe94253-842b-42d5-b6b7-7e524b8d1216
- format : pdf

```HTML
http://34.124.247.20:8080/api/v1/report/e-ticket?orderID=efe94253-842b-42d5-b6b7-7e524b8d1216&format=pdf
```

#### Response
<img src="https://content.pstmn.io/b1763834-6f50-4f2f-b621-66151a676919/U2NyZWVuc2hvdCAoMTQyKS5wbmc=" alt="e-ticket">

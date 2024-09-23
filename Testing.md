Create Akademiki:

POST http://localhost:8080/take/akademiki
{
  "nazwa": "Dormitory A",
  "adres": "123 University St.",
  "telefon": "123456789"
}

Get Akademiki by ID:
GET http://localhost:8080/take/akademiki/<id>

Update Akademiki:
PUT http://localhost:8080/take/akademiki
{
  "id_akademika": 1,
  "nazwa": "Dormitory A Updated",
  "adres": "123 University St.",
  "telefon": "987654321"
}


Delete Akademiki by ID:
DELETE http://localhost:8080/take/akademiki/<id>
__
Pokoje

Create Pokoje:

POST http://localhost:8080/take/pokoje

{
  "numer_pokoju": 101,
  "ilosc_dostepnych_miejsc": 2,
  "ilosc_lozek": 2,
  "akademiki": {
    "id_akademika": 1
  }
}


Get Pokoje by ID:

GET http://localhost:8080/take/pokoje/<id_pokoju>

Update a Pokoje:

PUT http://localhost:8080/take/pokoje

{
  "id_pokoju": 1,
  "numer_pokoju": 303,
  "ilosc_dostepnych_miejsc": 2,
  "ilosc_lozek": 2,
  "akademiki": {
    "id_akademika": 1
  }
}

Delete a Pokoje by ID:

DELETE http://localhost:8080/take/pokoje/<id_pokoju>
TIP — Wczoraj o 23:24
__
Studenci

Create a Studenci:

POST http://localhost:8080/take/studenci

{
  "imie": "Jan",
  "nazwisko": "Kowalski",
  "numer_indeksu": "123456",
  "email": "jan.kowalski@example.com",
  "pokoj": {
    "id_pokoju": 1
  }
}

Get a Studenci by ID:

GET http://localhost:8080/take/studenci/<id_studenta>

Update a Studenci:

PUT http://localhost:8080/take/studenci

{
  "id_studenta": 1,
  "imie": "Jan",
  "nazwisko": "Kowalski Updated",
  "numer_indeksu": "123456",
  "email": "jan.kowalski_updated@example.com",
  "pokoj": {
    "id_pokoju": 1
  }
}

Delete a Studenci by ID:

DELETE http://localhost:8080/take/studenci/<id_studenta>
TIP — Wczoraj o 23:41
__
Rezerwacja

Create a Rezerwacja:

POST http://localhost:8080/take/rezerwacje

{
  "data_rozpoczecia": "2024-09-01",
  "data_zakonczenia": "2024-09-15",
  "student": {
    "id_studenta": 1
  }
}

Get a Rezerwacja by ID:

GET http://localhost:8080/take/rezerwacje/<id_rezerwacji>

Update a Rezerwacja:

PUT http://localhost:8080/take/rezerwacje

{
  "id_rezerwacji": 1,
  "data_rozpoczecia": "2024-09-02",
  "data_zakonczenia": "2024-09-16",
  "student": {
    "id_studenta": 1
  }
}

Delete a Rezerwacja by ID:

DELETE http://localhost:8080/take/rezerwacje/<id_rezerwacji>
__
Oplaty

Create an Oplaty:

POST http://localhost:8080/take/oplaty

{
  "kwota": 150.00,
  "waluta": "PLN",
  "data_oplaty": "2024-09-01",
  "data_nastepnej_oplaty": "2024-10-01",
  "rezerwacja": {
    "id_rezerwacji": 1
  }
}

Get an Oplaty by ID:

GET http://localhost:8080/take/oplaty/<id_oplaty>

Update an Oplaty:

PUT http://localhost:8080/take/oplaty

{
  "id_oplaty": 1,
  "kwota": 200.00,
  "waluta": "PLN",
  "data_oplaty": "2024-09-05",
  "data_nastepnej_oplaty": "2024-10-05",
  "rezerwacja": {
    "id_rezerwacji": 1
  }
}

Delete an Oplaty by ID:

DELETE http://localhost:8080/take/oplaty/<id_oplaty>
# Bull V$ Bear - The Battle 

## Introduction
Bull V$. Bear - the Battle is a PvP game that aims to provide an interactive and educational experience in the world of forex trading. This game brings together multiple players who compete against each other by placing bets on currency pairs. The motivation behind developing Bull V$. Bear - the Battle was to create a platform that combines the excitement of PvP gameplay with the fundamental understanding of chart analysis, long positions, and short positions in the financial market.


## Technologies
- Spring Boot
- Spring Data
  - Postgres
  - JPA
  - Google Cloud SQL
- Mockito
- JUnit Juniper
- Gradle
- Alpha Vantage API
- REST

## Components
Our main component is the [game package](src/main/java/ch/uzh/ifi/hase/soprafs23/Game). It includes the [game](src/main/java/ch/uzh/ifi/hase/soprafs23/Game/Game.java) entity itself that is stored in our database as well as all classes needed for the state pattern. 
We use the state pattern to control the behaviour of a game object.

The heart and soul of our application is the [InstructionManager](src/main/java/ch/uzh/ifi/hase/soprafs23/Betting/InstructionManager.java) class. It contains the core logic for calculating the result of a betting round. The math for those calculations is based on the concept of [abelian groups](http://en.wikipedia.org/wiki/Abelian_group).

Another important component is the [ChartAPI](src/main/java/ch/uzh/ifi/hase/soprafs23/Forex/ChartAPI.java) class. This class is responsible for fetching the forex information in real time from the forex markets. It makes use of an external open-source package for fetching and interpreting the response from the API call.

## Launch and Deployment
Our application can be built by executing the following command. Prior to running the server locally, the environment must be set to “dev” in the [application.properties](src/main/resources/application.properties)  file:
```bash
./gradlew build
```
The application can be run by executing the following command:
```bash
./gradlew bootRun
```

The server will be launched at:  `localhost:8080`

The test suite can be run from inside an IDE (e.g., IntelliJ) by running the test package. Alternatively, the tests can be run by executing the following command:
```bash
./gradlew test
```

The application is hosted on Google Clouds App engine and is deployed automatically with every push to the main branch. The Postgres database is hosted on Google Cloud SQL.

## Roadmap
Potentially interesting additions to our project could be:
- Implementation of Websockets
- Tracking the effects of powerups (e.g., track who was affected by a powerup and how)  
- Adding stocks in addition to forex


## Authors and Acknowledgements
This game was possible thanks to following students, who designed and implemented it for the bachelor course **Software Engineering Lab** at UZH, spring 2023.

* **Cedric Egon Von Rauscher** - *Frontend*
* **Christian Berger** - *Frontend*
* **Maria Letizia Jannibelli** - *Frontend*
* **Stefan Richard Saxer** - *Backend*
* **Josep Cunquero Orts** - *Backend*

We are grateful to Jerome Maier for his consistently helpful guidance and insightful comments.

## Licence
This project is licensed under [Apache-2.0](LICENSE).


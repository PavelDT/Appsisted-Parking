# Appsisted-Parking

Appsisted-parking is a system that to solve the problem of high volume parking during a small time frame. It does so by tracking busyness state of parking sites at a location and making recommendations to its users on where it would be most appropriate for them to park based on user preference settings and parking spot availability.

## Setup

The host system requires the following pre-requisites:
- python 2.7
- git
- Java 8 (Preferably OpenJDK)
- Android studio (for emulation)
- IntelliJ IDEA (optional) or maven

### Setting up Cassandra

1. Download Cassandra 4.0 (https://cassandra.apache.org/download/)
2. Unzip apache-cassandra to the desired location
3. Open the folder and run bin/cassandra.bat

The database will store the data it needs in `<install location>/data` by default.

### Setting up the Spring Web-Service

1. Clone the project using git.
```
git clone https://github.com/PavelDT/appsisted-parking
```
2. Import the project into IntelliJ IDEA
3. Build the project using IntelliJ if installed or alternatively using maven.
```bash
# run this only if intellij isn't being used
cd <project-dir>
mvn package
```
4. Run the main function in `AppsistedParkingApplication`.

The web-service needs Cassandra in order to function properly.

### Setting up the Appsisted-parking Android App
1. Clone the project using git if you didn't do so in step 2.
```
git clone https://github.com/PavelDT/appsisted-parking
```
2. Import the Project found in `<download_dir>/app` into Android Studio.
3. Provide a Google API Key in the `src/main/AndroidManifest.xml` meta tag. Details on how get a key [here](https://developers.google.com/maps/documentation/embed/get-api-key).
4. Run the app using Android Studio.

The android app needs the web-service in order to function properly.

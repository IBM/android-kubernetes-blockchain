## Application Setup

* Install **maven** and **mongodb** on system.
* Verify the **executionURL**, **resultURL** urls from **ExecutionApp.java** and **ResultGenerator.java**.
* Make sure your are connecting to correct **mongodb** instance and **dbName** are same in **ExecutionApp.java** and **ResultGenerator.java**.
* Open terminal and run the following commands to setup the project
```bash
mvn compile
```
* Now run the test result loader Application
```bash
mvn exec:java -Dexec.mainClass="secretApp.testApp.ResultGenerator"
```
* In a new terminal,run the execution cli app
```bash
mvn exec:java -Dexec.mainClass="secretApp.testApp.ExecutionApp"
```
>Note: To view the results you can download robomongo/ Robo 3T

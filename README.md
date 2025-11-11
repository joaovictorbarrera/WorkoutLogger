# WorkoutLogger
Store, edit, delete, and convert Workout data with the WorkoutLogger Database Management System.

![Alt text](https://image.prntscr.com/image/T3uiWR7aRgOWP5S_FH76cg.png "Workout Logger Website")

This project relies on SQLite databases to hold and persist data.

The user must provide the database location in an application.properties file in the following format:
```
spring.datasource.url=jdbc:sqlite:PATH_TO_YOUR_DATABASE.db
```

Project built with Maven. Use Maven Install to compile all dependencies into an Uber JAR file.

# WorkoutLogger
Store, edit, delete, and convert Workout data with the WorkoutLogger Database Management System.

![Alt text](https://image.prntscr.com/image/C5d3B7tcS3ySp0taPughjw.png "Workout Logger Website")


This project relies on SQLite databases to hold its data.
The user will be prompted for a database file path, and the file needs to exist somewhere in the server-side filesystem.

A valid database contains a Workout Table with the following schema:

```sql
CREATE TABLE Workout (
    id INTEGER PRIMARY KEY AUTOINCREMENT CHECK (id > 0),
    name TEXT NOT NULL CHECK(length(name) <= 50),
    distance REAL NOT NULL CHECK(distance >= 0),
    unit TEXT NOT NULL,
    startDateTime TEXT NOT NULL,
    duration INTEGER NOT NULL CHECK(duration >= 1),
    notes TEXT CHECK(length(notes) <= 200)
);
```

Project built with Maven. Use Maven Install to compile all dependencies into an Uber JAR file.

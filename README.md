# WorkoutLogger

### Workout ID Information
The final product will have a website in which the WorkoutIDs will be invisible to the user.
The records will have an ID behind the scenes, but the user will not know about the ID visually.
An ID will be automatically generated for a record when it's added either through a file import or adding manually.

### File Import Documentation
The program can read any kind of text-based file.

File imports do not expect WorkoutID. They only expect the 6 points of data:

name, startDateTime, duration, distance, unit, notes

These should be comma-separated without spaces.

**Here is an example:**
- Morning Run,2025-10-07T06:30,45,5.0,KILOMETERS,Felt great
- Evening Walk,2025-10-07T18:15,30,2.0,MILES,Relaxing walk
- Lunch Jog,2025-10-07T12:00,25,3.5,KILOMETERS,Quick jog before work

### File Export Documentation

The program will only export to .txt and .csv file paths.
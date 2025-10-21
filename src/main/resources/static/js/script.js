console.log("Workout Logger page loaded");

const reloadWorkoutsButton = document.getElementById("reload-workouts");
const workoutTableBody = document.getElementById("workoutsTableBody");
const addWorkoutButton = document.getElementById("add-workout");

refreshWorkouts();

reloadWorkoutsButton.addEventListener("click", refreshWorkouts)

addWorkoutButton.addEventListener("click", () => {
    fetch("/api/WorkoutsCreate", {
        method: "POST",
        body: JSON.stringify({
            "name": "Morning Run",
            "startDateTime": "2025-10-21T07:30",
            "duration": 45,
            "distance": 5.0,
            "unit": "KILOMETERS",
            "notes": "Felt good"
        }),
        headers: {"Content-Type": "application/json"}
    })
    .then(response => console.log)
})

function refreshWorkouts() {
    workoutTableBody.innerText = ""

    fetch("/api/WorkoutsGet")
        .then(response => response.json())
        .then(data => {
            data.forEach(workout => {
                const tr = document.createElement("tr");

                const ID = document.createElement("td");
                ID.innerText = workout.id;
                tr.appendChild(ID);

                const name = document.createElement("td");
                name.innerText = workout.name;
                tr.appendChild(name);

                const startDateTime = document.createElement("td");
                startDateTime.innerText = workout.startDateTime;
                tr.appendChild(startDateTime);

                const duration = document.createElement("td");
                duration.innerText = workout.duration;
                tr.appendChild(duration);

                const distance = document.createElement("td");
                distance.innerText = workout.distance;
                tr.appendChild(distance);

                const unit = document.createElement("td");
                unit.innerText = workout.unit;
                tr.appendChild(unit);

                const notes = document.createElement("td");
                notes.innerText = workout.notes;
                tr.appendChild(notes);

                workoutTableBody.appendChild(tr);
            })
        })
}
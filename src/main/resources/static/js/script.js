const reloadWorkoutsButton = document.getElementById("reload-workouts");
const workoutTableBody = document.getElementById("workoutsTableBody");

const createWorkoutButton = document.getElementById("create-workout");
const updateWorkoutButton = document.getElementById("update-workout");
const deleteWorkoutButton = document.getElementById("delete-workout");
const formResultMessage = document.getElementById("formResultMessage");

const setDatabaseButton = document.getElementById("set-database");
const databaseName = document.getElementById("database-name");

const convertToMilesButton = document.getElementById("convert-to-miles");
const convertToKilometersButton = document.getElementById("convert-to-kilometers");

const searchForm = document.getElementById("search-form");
const searchInput = document.getElementById("search-input");

let workouts = {}
let selectedRow = null;

checkDatabaseConnection()

reloadWorkoutsButton.addEventListener("click", refreshWorkouts)

searchForm.addEventListener("submit", (e) => {
    e.preventDefault();

    let query = searchInput.value.trim();
    if (!query) query = "";

    fetch(`/api/workout/getByName?name=${encodeURIComponent(query)}`)
    .then(async response => {
        if (response.ok) {
            const data = await response.json();
            populateWorkoutsTable(data);
        } else {
            const errorBody = await response.json();
            alert("Search failed: " + (errorBody.error || "Unknown error"));
        }
    })
    .catch(err => {
        console.error(err);
        alert("Error fetching workouts.");
    })
});

createWorkoutButton.addEventListener("click", () => {
    resetResultMessage()

    const formData = getWorkoutFormData()
    if (!formData) return;

    fetch("/api/workout/create", {
        method: "POST",
        body: JSON.stringify(formData),
        headers: {"Content-Type": "application/json"}
    })
    .then(async response => {
        if (response.ok) {
            formResultMessage.textContent = "Workout added successfully!";
            formResultMessage.classList.add("success");
        } else {
            const errorBody = await response.json()
            formResultMessage.textContent = errorBody.error || "Failed to add workout.";
            formResultMessage.classList.add("error");
        }
    })
    .catch(console.log)
    .finally(() => {
        refreshWorkouts()
        clearWorkoutForm()
    })
})

updateWorkoutButton.addEventListener("click", () => {
    resetResultMessage()

    if (!selectedRow) {
        formResultMessage.textContent = "No workout selected to update.";
        formResultMessage.classList.add("error");
        return;
    }

    const id = getIdFromSelectedRow()
    if (!id) return;

    const formData = getWorkoutFormData()
    if (!formData) return;

    fetch("/api/workout/updateByID", {
        method: "PUT",
        body: JSON.stringify({
            id,
            ...formData
        }),
        headers: {"Content-Type": "application/json"}
    })
        .then(async response => {
            if (response.ok) {
                formResultMessage.textContent = "Workout updated successfully!";
                formResultMessage.classList.add("success");
            } else {
                const errorBody = await response.json();
                formResultMessage.textContent = errorBody.error || "Failed to update workout.";
                formResultMessage.classList.add("error");
            }
        })
        .catch(console.error)
        .finally(() => {
            refreshWorkouts()
            clearWorkoutForm()
        })
});

deleteWorkoutButton.addEventListener("click", () => {
    resetResultMessage()

    if (!selectedRow) {
        formResultMessage.textContent = "No workout selected to update.";
        formResultMessage.classList.add("error");
        return;
    }

    const id = getIdFromSelectedRow()
    if (!id) return;

    if (confirm("Are you sure you want to delete this workout?") === false) return

    fetch("/api/workout/deleteByID", {
        method: "DELETE",
        body: JSON.stringify({
            "id": id
        }),
        headers: {"Content-Type": "application/json"}
    })
    .then(async response => {
        if (response.ok) {
            formResultMessage.textContent = "Workout deleted successfully!";
            formResultMessage.classList.add("success");
        } else {
            const errorBody = await response.json()
            const errorMessage = errorBody.error
            window.alert(errorMessage);
        }
    })
    .catch(console.log)
    .finally(() => {
        refreshWorkouts()
        clearWorkoutForm()
    })
})

convertToMilesButton.addEventListener("click", () => {
    convertUnits("MILES")
})

convertToKilometersButton.addEventListener("click", () => {
    convertUnits("KILOMETERS")
})

setDatabaseButton.addEventListener("click", async () => {
    const sqlitePath = prompt("Enter SQLite database path:");
    if (!sqlitePath) return;

    try {
        const response = await fetch('/api/workout/database/connect?path=' + encodeURIComponent(sqlitePath), {
            method: 'POST'
        });

        if (response.ok) {
            setDatabaseButton.classList.add("success");
            enableUI();
            refreshWorkouts();
            alert("Database connected successfully!");
            checkDatabaseConnection();
        } else {
            const errorBody = await response.json();
            window.alert("Failed to connect database:\n" + errorBody.error);
        }
    } catch (err) {
        window.alert("Error connecting to database:\n" + err.message);
    }
});

function checkDatabaseConnection() {
    fetch("/api/workout/database/name")
    .then(async response => {
        response = await response.json();

        if (response.name) {
            setDatabaseButton.classList.add("success");
            enableUI();
            refreshWorkouts();
            databaseName.style.display = "block";
            databaseName.innerText = "Connnected to " + response.name;
        }
    })
}

function convertUnits(unitType) {
    if (confirm("Are you sure you want to convert all workouts to " + unitType + "?") === false) return

    fetch("/api/workout/convertUnits", {
        method: "PUT",
        body: JSON.stringify(unitType),
        headers: {"Content-Type": "application/json"}
    })
    .then(async response => {
        if (!response.ok) {
            const errorBody = await response.json()
            const errorMessage = errorBody.error
            window.alert(errorMessage);
        }
    })
    .catch(console.log)
    .finally(refreshWorkouts)
}

function refreshWorkouts() {
    fetch("/api/workout/getAll")
    .then(async response => {
        if (response.ok) {
            const data = await response.json();
            populateWorkoutsTable(data);
        } else {
            const errorBody = await response.json();
            alert("Search failed: " + (errorBody.error || "Unknown error"));
        }
    })
    .catch(err => {
        console.error(err);
        alert("Error fetching workouts.");
    })
}

function selectRow(e) {
    // Remove previous selection highlight
    if (selectedRow !== null) {
        selectedRow.classList.remove("selected-row");

        // If clicking the selected row again, unselect it
        if (e.currentTarget === selectedRow) {
            selectedRow = null;
            updateSelectedButtonsVisibility();
            return;
        }
    }

    // Set new selected row
    selectedRow = e.currentTarget;
    selectedRow.classList.add("selected-row");
    updateSelectedButtonsVisibility();

    // Insert all values from selected row into Workout Editor
    document.getElementById("workoutName").value = selectedRow.dataset.name;
    document.getElementById("startDateTime").value = selectedRow.dataset.startDateTime;
    document.getElementById("duration").value = selectedRow.dataset.duration;
    document.getElementById("distance").value = selectedRow.dataset.distance;
    document.getElementById("unit").value = selectedRow.dataset.unit;
    document.getElementById("notes").value = selectedRow.dataset.notes;
}

// Reveals/hides UPDATE and DELETE buttons if there is a selected row
function updateSelectedButtonsVisibility() {
    const visible = selectedRow !== null;

    if (visible) {
        updateWorkoutButton.classList.remove("hidden");
        deleteWorkoutButton.classList.remove("hidden");
    } else {
        updateWorkoutButton.classList.add("hidden");
        deleteWorkoutButton.classList.add("hidden");
    }
}

function getIdFromSelectedRow() {
    if (!selectedRow) return null;
    return parseInt(selectedRow.dataset.id);
}

// Parses workout table data from the API into HTML table rows and adds to the screen
function populateWorkoutsTable(data) {
        // Reset Variables
        workoutTableBody.innerText = ""
        selectedRow = null;
        workouts = data;
        updateSelectedButtonsVisibility()

        // For every workout, build a table row and append to the table body
        data.forEach(workout => {
            const tr = document.createElement("tr");
            tr.addEventListener("click", selectRow);

            tr.dataset.id = workout.id;
            tr.dataset.name = workout.name;
            tr.dataset.startDateTime = workout.startDateTime;
            tr.dataset.duration = workout.duration;
            tr.dataset.distance = workout.distance;
            tr.dataset.unit = workout.unit;
            tr.dataset.notes = workout.notes;

            const name = document.createElement("td");
            name.innerText = workout.name;
            tr.appendChild(name);

            const startDateTime = document.createElement("td");
            startDateTime.innerText = formatDateTime(workout.startDateTime);
            tr.appendChild(startDateTime);

            const duration = document.createElement("td");
            duration.innerText = workout.duration;
            tr.appendChild(duration);

            const distance = document.createElement("td");
            distance.innerText = workout.distance.toFixed(2);
            tr.appendChild(distance);

            const unit = document.createElement("td");
            unit.innerText = workout.unit;
            tr.appendChild(unit);

            const notes = document.createElement("td");
            notes.innerText = workout.notes;
            tr.appendChild(notes);

            workoutTableBody.appendChild(tr);
        })
}

// Resets error/success message
function resetResultMessage() {
    formResultMessage.textContent = "";
    formResultMessage.classList.remove("error", "success");
}

// Get all values from workout form and return data as an object
function getWorkoutFormData() {
    const name = document.getElementById("workoutName").value.trim();
    const startDateTime = document.getElementById("startDateTime").value;
    const duration = parseInt(document.getElementById("duration").value);
    const distance = parseFloat(document.getElementById("distance").value);
    const unit = document.getElementById("unit").value.toUpperCase();
    const notes = document.getElementById("notes").value.trim();

    // Basic validation
    if (!name || !startDateTime || isNaN(duration) || isNaN(distance)) {
        formResultMessage.textContent = "Please fill out all required fields correctly.";
        formResultMessage.classList.add("error");
        return null;
    }

    return {
        name,
        startDateTime,
        duration,
        distance,
        unit,
        notes
    }
}

function clearWorkoutForm() {
    document.getElementById("workoutName").value = "";
    document.getElementById("startDateTime").value = "";
    document.getElementById("duration").value = "";
    document.getElementById("distance").value = "";
    document.getElementById("unit").value = "KILOMETERS";
    document.getElementById("notes").value = "";
}

function enableUI() {
    document.querySelectorAll('[disabled]').forEach(el => el.removeAttribute('disabled'));
}

// Transforms "2025-10-07T18:30:00" into Oct 07, 2025 18:30
function formatDateTime(dateString) {
    const date = new Date(dateString);

    const options = {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
    };

    return new Intl.DateTimeFormat('en-US', options).format(date);
}
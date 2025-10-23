const reloadWorkoutsButton = document.getElementById("reload-workouts");
const workoutTableBody = document.getElementById("workoutsTableBody");

const createWorkoutButton = document.getElementById("create-workout");
const updateWorkoutButton = document.getElementById("update-workout");
const deleteWorkoutButton = document.getElementById("delete-workout");
const formResultMessage = document.getElementById("formResultMessage");

const convertToMilesButton = document.getElementById("convert-to-miles");
const convertToKilometersButton = document.getElementById("convert-to-kilometers");

const importForm = document.getElementById("importForm")
const importButton = document.getElementById("import-workouts");
const workoutFile = document.getElementById("workoutFile");

const exportWorkoutsButton = document.getElementById("export-workouts");

const searchForm = document.getElementById("search-form");
const searchInput = document.getElementById("search-input");

let workouts = {}
let selectedRow = null;

refreshWorkouts();

reloadWorkoutsButton.addEventListener("click", refreshWorkouts)

searchForm.addEventListener("submit", (e) => {
    e.preventDefault();

    let query = searchInput.value.trim();
    if (!query) query = "";

    fetch(`/api/WorkoutsGetByName?name=${encodeURIComponent(query)}`)
    .then(async response => {
        if (response.ok) {
            const data = await response.json();
            populateWorkoutsTable(data);
        } else {
            const errorBody = await response.json();
            alert("Search failed: " + (errorBody.message || "Unknown error"));
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

    fetch("/api/WorkoutsCreate", {
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
            formResultMessage.textContent = errorBody.message || "Failed to add workout.";
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

    const formData = getWorkoutFormData()
    if (!formData) return;

    fetch("/api/WorkoutsUpdateByID", {
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
                formResultMessage.textContent = errorBody.message || "Failed to update workout.";
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

    if (confirm("Are you sure you want to delete this workout?") === false) return

    fetch("/api/WorkoutsDeleteByID", {
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
            const errorMessage = errorBody.message
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

function convertUnits(unitType) {
    if (confirm("Are you sure you want to convert all workouts to " + unitType + "?") === false) return

    fetch("/api/ConvertUnits", {
        method: "PUT",
        body: JSON.stringify(unitType),
        headers: {"Content-Type": "application/json"}
    })
    .then(async response => {
        if (!response.ok) {
            const errorBody = await response.json()
            const errorMessage = errorBody.message
            window.alert(errorMessage);
        }
    })
    .catch(console.log)
    .finally(refreshWorkouts)
}

function refreshWorkouts() {
    fetch("/api/WorkoutsGet")
    .then(async response => {
        if (response.ok) {
            const data = await response.json();
            populateWorkoutsTable(data);
        } else {
            const errorBody = await response.json();
            alert("Search failed: " + (errorBody.message || "Unknown error"));
        }
    })
    .catch(err => {
        console.error(err);
        alert("Error fetching workouts.");
    })
}

workoutFile.addEventListener("change", () => {
    if (!workoutFile.files.length) {
        alert("Please choose a file first!");
        return;
    }

    const formData = new FormData();
    formData.append("file", workoutFile.files[0]);

    fetch("/api/ImportWorkouts", {
        method: "POST",
        body: formData
    })
    .then(async response => {
        if (response.ok) {
            alert("File imported successfully");
        } else {
            const err = await response.json();
            alert("Import failed: " + err.message);
        }
    })
    .catch(console.error)
    .finally(() => {
        refreshWorkouts()
        importForm.reset();
    })
});

exportWorkoutsButton.addEventListener("click", () => {
    fetch("/api/ExportWorkouts", {
        method: "POST"
    })
    .then(async response => {
        if (!response.ok) {
            const err = await response.json();
            alert("Failed to export workouts: " + err.message);
            return;
        }

        // Get the file as a Blob
        const blob = await response.blob();

        // Create a temporary link to download and use a DOM element to action it
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = "workouts.csv";
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
    })
    .catch(console.log)
    .finally(refreshWorkouts)


});

// Use the regular button to open the file input
importButton.addEventListener("click", () => {
    workoutFile.click();
});

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

    // Populate the form fields with the selected row's data
    const cells = selectedRow.children;

    document.getElementById("workoutName").value = cells[0].innerText;
    document.getElementById("startDateTime").value = cells[1].innerText;
    document.getElementById("duration").value = cells[2].innerText;
    document.getElementById("distance").value = cells[3].innerText;
    document.getElementById("unit").value = cells[4].innerText;
    document.getElementById("notes").value = cells[5].innerText;
}

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
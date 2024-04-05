
let isClockedIn = false; // Flag to track clock-in status
let currentConfirmAction = null;

document.addEventListener('DOMContentLoaded', () => {
    let clockState = document.body.getAttribute('data-clock-state');
    console.log("Current clock-in state: " + clockState);
    isClockedIn = clockState === 'clock_out'; 
    updateClockButton(document.querySelector('.clock-button'), isClockedIn);
});

// Utility function to get PST date as ISO string
function getLocalIsoDate() {
    const now = new Date();
    const offsetInMilliseconds = 7 * 60 * 60 * 1000; // 8 hours in milliseconds
    const pstDate = new Date(now.getTime() - offsetInMilliseconds);
    return pstDate.toISOString().slice(0, -5); // Slice to remove milliseconds and Z
}

function confirmAction(callback, modalInstance) {
    console.log("confirmAction called")
    return function() { // Return a function that retains the callback and modal instance
        callback();  // Execute the callback if confirmed
        modalInstance.hide(); // Close the modal
        // Clean up: Remove this event listener after it's invoked
        this.removeEventListener('click', confirmAction);
    };
}

// Function to display the confirmation modal
// It takes a callback function to execute if user confirms

function showConfirmationModal(callback) {
    const modalElement = document.getElementById('confirmationModal');
    const modal = new bootstrap.Modal(modalElement);
    
    // Update modal content
    const modalText = document.querySelector('#confirmationModal .modal-body');
    modalText.innerHTML = `Are you sure you want to ${isClockedIn ? "clock out" : "clock in"}?<br>Current time: ${new Date().toLocaleTimeString()}`;
    modal.show();

    // Set event listener for confirm button
    const confirmButton = document.getElementById('confirmButton');
    // Remove previous listener if it exists
    if (currentConfirmAction) {
        confirmButton.removeEventListener('click', currentConfirmAction);
    }
    // Create a new listener function
    currentConfirmAction = () => {
        callback();  // Execute the callback if confirmed
        modal.hide(); // Close the modal
        confirmButton.removeEventListener('click', currentConfirmAction); // Remove listener after invocation
    };
    confirmButton.addEventListener('click', currentConfirmAction);
}

function closeModal() {
    const modalElement = document.getElementById('confirmationModal');
    const modalInstance = bootstrap.Modal.getInstance(modalElement);
    modalInstance.hide();
}

// Main function to handle clock in/out actions
function handleClockInOut(event, button) {
    console.log("handleClockInOut called")
    event.preventDefault(); // Prevent the default action
    showConfirmationModal(() => {
        // Fetch and process location only after user confirms in the modal
        fetchAndProcessLocation(address => postClockAction(address, button));
    });
}

// Function to handle the geolocation and address fetching
function fetchAndProcessLocation(callback) {
    //Check if the browser supports geolocation. If it does, then we continue in the block
    if (navigator.geolocation) {
        //Requests the current location of the device
        navigator.geolocation.getCurrentPosition(position => {
            const {latitude, longitude} = position.coords;
            
            //TODO: Change the API key to be hidden in environment variables
            const geocodeApiUrl = `https://maps.googleapis.com/maps/api/geocode/json?latlng=${latitude},${longitude}&key=AIzaSyD7W1stQyxkMS1msMvHXRHBPDltzAXZh3g`;
            //Makes an HTTP GET request. fetch returns a promise that resolves with the response to this request
            fetch(geocodeApiUrl)
                //Takes the response and uses .json to parse the response body as JSON
                .then(response => response.json())
                //Receives the parsed JSON data above. "data" is the response from the google maps API
                .then(data => {
                    if (data.status === "OK" && data.results[0]) {
                        callback(data.results[0].formatted_address); // Proceed with the provided address
                    } else {
                        throw new Error('Failed to retrieve address');
                    }
                }).catch(error => {
                    console.error('Geolocation to address error:', error);
                });
        }, error => {
            console.error('Geolocation error:', error);
            alert('Please enable location services to use this feature.');
        });
    //Error message if the browser does not support geolocation
    } else {
        console.error('Geolocation is not supported by this browser.');
        alert('Geolocation is not supported by this browser.');
    }
}

// Function to be called after location is fetched and user confirmed
function postClockAction(address, button) {
    const technicianId = document.body.getAttribute('tech-id');
    const intendedAction = isClockedIn ? "clock_out" : "clock_in";  // The intended action based on current state

    fetch('/technician/clock', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            technicianId: technicianId,
            action: intendedAction,
            location: address,
            createdAt: getLocalIsoDate()
        })
    })
    .then(response => {
        if (!response.ok) throw new Error('Network response was not ok.');
        return response.json();
    })
    .then(data => {
        console.log('Success:', data);
        // Toggle the state after confirmation from the backend
        isClockedIn = (intendedAction === "clock_in");  // Update based on the action sent, not the old state
        button.setAttribute('data-clocked-in', isClockedIn.toString());
        updateClockButton(button, isClockedIn);
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

function updateClockButton(button, isClockedIn) {
    if (isClockedIn) {
        button.innerHTML = '<i class="fa-solid fa-clock fs-4"></i> Clock Out';
        button.setAttribute('data-clocked-in', 'false');
        button.classList.add('blinking');
    } else {
        button.innerHTML = '<i class="fa-solid fa-clock fs-4"></i> Clock In';
        button.setAttribute('data-clocked-in', 'true');
        button.classList.remove('blinking');
    }
}
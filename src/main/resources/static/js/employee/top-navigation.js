
let buttonClicked = false;

function toggle_clock(event, button) {
    // Prevent the default behavior of the event
    event.preventDefault();

    let icon = button.querySelector('.clock');

    // Toggle text-danger class
    icon.classList.toggle('text-danger');

    // Toggle fa-fade class
    icon.classList.toggle('fa-fade');
    let coordinatesDiv = document.getElementById('coordinates');
    // Check if button was clicked before
    if (buttonClicked) {
        // The button was clicked before, attempt to display current location coordinates and time
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(position) {
                // Get the coordinates of the current position
                let lat = position.coords.latitude;
                let lng = position.coords.longitude;

                // Get the current time
                let now = new Date();
                let clockoutTime = now.toLocaleTimeString(); 

                // Update the coordinates container with the location and time
                coordinatesDiv.innerText += ' Latitude: ' + lat + ', Longitude: ' + lng + ', Clock Out: ' + clockoutTime;

                // Make the coordinates div visible
                coordinatesDiv.style.display = 'block';
            }, function(error) {
                // Handle location error
                console.error('Geolocation error: ' + error.message);
                let coordinatesDiv = document.getElementById('coordinates');
                coordinatesDiv.innerText = 'Error getting location: ' + error.message;
                coordinatesDiv.style.display = 'block'; // Show the div to display the error message
            });
        } else {
            console.error('Geolocation is not supported by this browser.');
            let coordinatesDiv = document.getElementById('coordinates');
            coordinatesDiv.innerText = 'Geolocation is not supported by this browser.';
            coordinatesDiv.style.display = 'block'; // Show the div even if geolocation is not supported
        }
    } else {
        buttonClicked = true;
        let clockInDate = new Date();
        let clockInTime = clockInDate.toLocaleTimeString();
        coordinatesDiv.innerText = 'Clock In: ' + clockInTime;

    }
}
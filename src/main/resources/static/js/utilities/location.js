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
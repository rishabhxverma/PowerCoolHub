function on_complete_button_clicked(dom) {
    const READY_STATE_DONE = 4;
    const HTTP_OK = 200;

    let button = dom;
    let jobId = dom.getAttribute('jobId');
    
    button.classList.add('disabled');
    button.setAttribute('disabled', 'disabled');

    fetchAndProcessLocation(address => {
        let xhr = new XMLHttpRequest();
        xhr.open('POST', jobId + '/complete');
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onreadystatechange = function () {
            // When response is returned.
            if (xhr.readyState === READY_STATE_DONE) {
                if (xhr.status === HTTP_OK) {
                    // Update status element if response is good
                    let completeStatus = document.getElementById('complete-status');
                    if (completeStatus) {
                        completeStatus.classList.remove('text-muted');
                        completeStatus.classList.add('text-success');
                    }
                } else {
                    console.error('Request failed:', xhr.status);
                }
            }
        };

        xhr.send(address);
    })
}

function on_direction_button_clicked(dom) {
    let mapUrl;
    let address = dom.getAttribute('customerAddress');

    // Apple device, use Apple Maps
    if (/(iPhone|iPod|iPad)/i.test(navigator.userAgent)) {
        mapUrl = "https://maps.apple.com/?q=" + encodeURIComponent(address);
    } 
    // Non-Apple device, use Google Maps
    else {
        mapUrl = "https://maps.google.com/?q=" + encodeURIComponent(address);
    }

    window.open(mapUrl, '_blank');
    // window.location.href = mapUrl;
}

function on_note_field_changed(dom) {
    let jobId = dom.getAttribute('jobId');
    let note = dom.value;

    let xhr = new XMLHttpRequest();
    xhr.open('POST', jobId + '/note/update');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function () {
        // When response is returned.
        if (xhr.readyState === READY_STATE_DONE) {
            if (xhr.status === HTTP_OK) {
                // Update status element if response is good
            } else {
                console.error('Request failed:', xhr.status);
            }
        }
    };

    xhr.send(note);
}
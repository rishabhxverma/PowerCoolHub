function on_complete_button_clicked(dom) {
    const READY_STATE_DONE = 4;
    const HTTP_OK = 200;

    let button = dom;
    button.classList.add('disabled');
    button.setAttribute('disabled', 'disabled');

    fetchAndProcessLocation(address => {
        let xhr = new XMLHttpRequest();
        xhr.open('POST', dom.getAttribute('jobId') + '/complete');
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

        xhr.send(JSON.stringify({ address: address }));
    })
}
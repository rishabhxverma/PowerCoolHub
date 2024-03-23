function on_filter_option_changed(dom) {
    const READY_STATE_DONE = 4;
    const HTTP_OK = 200;

    let selectedOption = dom.value;
    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/technician/history/filter?by=' + encodeURIComponent(selectedOption));
    xhr.setRequestHeader('Content-Type', 'text/html');
    xhr.onreadystatechange = function () {
        // When response is returned.
        if (xhr.readyState === READY_STATE_DONE) {
            if (xhr.status === HTTP_OK) {
                // Rerender the table.
                document.getElementById('history-table-data').innerHTML = xhr.responseText;
            } else {
                console.error('Request failed:', xhr.status);
            }
        }
    };
    xhr.send(JSON.stringify({ filter: selectedOption }));
}
function toggle_clock(event, button) {
    // Prevent the default behavior of the event
    event.preventDefault();

    let icon = button.querySelector('.clock');

    // Toggle text-danger class
    icon.classList.toggle('text-danger');

    // Toggle fa-fade class
    icon.classList.toggle('fa-fade');;
}
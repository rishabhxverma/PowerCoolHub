//functions for auto complete, and functions for validating the form
// This file is used in the addCustomer.html and editCustomer.html

function initAutocomplete() {
  var input = document.getElementById("address");
  var autocomplete = new google.maps.places.Autocomplete(input);
}
window.onload = initAutocomplete;

window.onload = initAutocomplete;

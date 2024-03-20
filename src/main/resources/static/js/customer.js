//functions for auto complete, and functions for validating the form
// This file is used in the addCustomer.html and editCustomer.html

function initAutocomplete() {
  var input = document.getElementById("address");
  var autocomplete = new google.maps.places.Autocomplete(input);
  var submitButton = document.getElementById("submit-button");

  // Disable the submit button initially
  submitButton.disabled = true;

  autocomplete.addListener("place_changed", function () {
    var place = autocomplete.getPlace();
    // Enable the submit button only if a valid place is selected
    if (place && place.formatted_address === input.value) {
      submitButton.disabled = false;
    } else {
      submitButton.disabled = true;
    }
  });

  input.addEventListener("input", function () {
    // Disable the submit button if the input is modified after autocomplete
    if (!input.getAttribute("data-autocomplete-used")) {
      submitButton.disabled = true;
      submitButton.style.backgroundColor = "grey";
    }
  });

  input.addEventListener("keydown", function () {
    // Mark the input as used by autocomplete when the user interacts with the autocomplete dropdown
    input.setAttribute("data-autocomplete-used", "true");
    submitButton.style.backgroundColor = "blue";
  });

  form.addEventListener("submit", function (event) {
    var place = autocomplete.getPlace();
    // Prevent form submission if the input value doesn't match a valid place
    if (!place || place.formatted_address !== input.value) {
      event.preventDefault();
      alert("Please enter a valid address.");
    }
  });
}

window.onload = initAutocomplete;

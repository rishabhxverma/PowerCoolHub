function initAutocomplete() {
  var input = document.getElementById("address");
  var autocomplete = new google.maps.places.Autocomplete(input);
}
window.onload = initAutocomplete;
function confirmUpdate(event) {
  event.preventDefault();
  if (confirm('Are you sure you want to update this customer?')) {
      var form = $('#editCustomerForm');
      $.ajax({
          url: form.attr('action'),
          type: 'POST',
          data: form.serialize(),
          success: function (response) {
              alert(response.message);

              window.location.href = '/customers/viewAll';
          },
          error: function (xhr, status, error) {
              var errorMessage = xhr.responseJSON && xhr.responseJSON.error ? xhr.responseJSON.error : "An error occurred during the update request.";
              alert(errorMessage);
          }
      });
  }
}
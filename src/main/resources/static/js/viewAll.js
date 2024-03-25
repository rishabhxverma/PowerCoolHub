document.getElementById("filter").addEventListener("change", function () {
    var selectedFilter = this.value;
    console.log(selectedFilter);
    fetch(`/customers/filterJson?filter=${selectedFilter}`)
      .then((response) => response.json())
      .then((data) => {
        // Clear the table
        var tbody = document.querySelector("tbody");
        while (tbody.firstChild) {
          tbody.removeChild(tbody.firstChild);
        }

        // Add the filtered customers to the table
        data.forEach((customer) => {
          var row = document.createElement("tr");

          // Add button if filter is "requesting-app"
          if (selectedFilter === "requesting-app") {
            var buttonCell = document.createElement("td");
            var button = document.createElement("button");
            button.textContent = "Book Appointment";
            button.classList.add("btn", "btn-primary");
            button.addEventListener("click", function () {
              // Open modal code goes here
              var jobModal = document.getElementById("jobModal");
              var modal = new bootstrap.Modal(jobModal);
              modal.show();
              console.log("Open modal for customer: " + customer.id);
              document.getElementById("customerId").value = customer.id;
            });
            buttonCell.appendChild(button);
            row.appendChild(buttonCell);
          }

          var nameCell = document.createElement("td");
          var nameLink = document.createElement("a");
          nameLink.href = `/customers/edit/${customer.id}`;
          nameLink.textContent = customer.name;
          nameCell.appendChild(nameLink);
          row.appendChild(nameCell);

          var addressCell = document.createElement("td");
          addressCell.textContent = customer.address;
          row.appendChild(addressCell);

          var phoneNumberCell = document.createElement("td");
          phoneNumberCell.textContent = customer.phoneNumber;
          row.appendChild(phoneNumberCell);

          var lastServicedCell = document.createElement("td");
          lastServicedCell.textContent = customer.lastServiced;
          row.appendChild(lastServicedCell);

          var nextServiceCell = document.createElement("td");
          nextServiceCell.textContent = customer.nextService;
          row.appendChild(nextServiceCell);

          var stateCell = document.createElement("td");
          stateCell.textContent = customer.state;
          row.appendChild(stateCell);

          var notesCell = document.createElement("td");
          notesCell.textContent = customer.notes;
          row.appendChild(notesCell);

          tbody.appendChild(row);
        });
      });
  });

// Calendar clicking for job date.....................................................
function resetColumnBackgrounds() {
    document.querySelectorAll('.calendar-top, .calendar-col').forEach(col => {
      col.style.backgroundColor = ""; // Reset background color of .calendar-top
    });
    // document.querySelectorAll('.calendar-col').forEach(col => {
    //   col.style.backgroundColor = ""; // Reset background color of .calendar-col
    // });
}

function changeColumnColor(date){
    resetColumnBackgrounds();
    let top = document.querySelector(`.week-date[datetime="${date}"]`).parentElement;
    let column = document.querySelector(`.calendar-col[datetime="${date}"]`);

    top.style.backgroundColor = "#e0e0e0";
    column.style.backgroundColor = "#f0f0f0";
}

// Selecting top and col to allow clicking on the calendar
document.querySelectorAll('.calendar-top').forEach(col => {
    col.addEventListener('click', (event) => { 
        let date = col.querySelector('.week-date').getAttribute('datetime');  
        document.querySelector('#dateService').value = date;
        changeColumnColor(date);
    });
});

document.querySelectorAll('.calendar-col').forEach(col => {
    col.addEventListener('click', (event) => {
        let date = col.getAttribute('datetime');
        document.querySelector('#dateService').value = date;
        changeColumnColor(date);
    });
});


// Event listener for .job elements
document.querySelectorAll('.job').forEach(job => {
    job.addEventListener('click', (event) => {
        // Stop the event from bubbling up to the column listeners
        event.stopPropagation();

        // maybe add job click function here 
    });
});


// Calendar clicking for job date.....................................................




// Enables clicking on user boxes to check them..........................................
let userBoxes = document.querySelectorAll('.user-box');
userBoxes.forEach(function(box) {
    box.querySelector('input[type="checkbox"]').addEventListener('change', function() {
        if (this.checked) {
        box.classList.add('checked');
        } else {
        box.classList.remove('checked');
        }
    });
});
// Uncheck all checkboxes when page is refreshed
var checkboxes = document.querySelectorAll('input[name="selectedUsers"]');
checkboxes.forEach(function(checkbox) {
    checkbox.checked = false;
});
// Enables clicking on user boxes to check them..........................................

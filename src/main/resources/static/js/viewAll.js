document.getElementById("filter").addEventListener("change", function () {
  var selectedFilter = this.value;
  fetch(`/customers/filterJson?filter=${selectedFilter}`)
    .then((response) => response.json())
    .then((data) => {
      // Clear the table body
      var tbody = document.querySelector("tbody");
      tbody.innerHTML = ""; // Clearing using innerHTML for simplicity

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
            var jobModal = document.getElementById("jobModal");
            var modal = new bootstrap.Modal(jobModal);
            modal.show();
            document.getElementById("customerId").value = customer.id;
            fetch(`/customers/${customer.id}/message`)
              .then((response) => response.text())
              .then((message) => {
                document.getElementById("message").value = message;
              });
          });
          buttonCell.appendChild(button);
          row.appendChild(buttonCell);
        } else {
          // If filter is not "requesting-app", hide the first column including title
          var emptyCell = document.createElement("td");
          row.appendChild(emptyCell);
        }

        // Append other cells
        appendCell(
          row,
          "a",
          "link-dark text-decoration-none",
          `/customers/edit/${customer.id}`,
          customer.name
        );
        appendCell(row, "td", "", "", customer.address);
        appendCell(row, "td", "", "", customer.phoneNumber);
        appendCell(row, "td", "", "", customer.nextService);
        appendCell(row, "td", "", "", customer.state);
        appendCell(row, "td", "", "", customer.message);

        // Append the row to the table body
        tbody.appendChild(row);
      });
    });
});

// Function to append cells
function appendCell(row, element, className, href, textContent) {
  var cell = document.createElement(element);
  if (className !== "") {
    cell.className = className;
  }
  if (href !== "") {
    var link = document.createElement("a");
    link.href = href;
    link.textContent = textContent;
    cell.appendChild(link);
  } else {
    cell.textContent = textContent;
  }
  row.appendChild(cell);
}

// Enables clicking on user boxes to check them..................
let techBoxes = document.querySelectorAll(".tech-box");
techBoxes.forEach(function (box) {
  box
    .querySelector('input[type="checkbox"]')
    .addEventListener("change", function () {
      if (this.checked) {
        box.classList.add("checked");
      } else {
        box.classList.remove("checked");
      }
    });
});

// Uncheck all checkboxes when page is refreshed
var checkboxes = document.querySelectorAll('input[name="technicianIds"]');
checkboxes.forEach(function (checkbox) {
  checkbox.checked = false;
});

//gets user name from userId
function getUserName(userId) {
  fetch(`/users/getUserName?userId=${userId}`)
    .then((response) => response.json())
    .then((data) => {
      console.log(data);
    });
}

function handleDateChange(date) {
  const techCheckboxDiv = document.getElementById("tech-checkbox");

  fetch("/jobs/getJobsCount?date=" + date)
    .then((response) => response.json())
    .then((jobCounts) => {
      // hashmap of userId->jobCount
      const techBoxes = document.querySelectorAll(".tech-box");

      techBoxes.forEach((techBox) => {
        const checkbox = techBox.querySelector('input[name="technicianIds"]');

        const userId = checkbox.value;

        const jobCountDiv = techBox.querySelector(".job-count");

        const count = jobCounts[userId];
        jobCountDiv.textContent = `Jobs: ${count}`;
      });
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

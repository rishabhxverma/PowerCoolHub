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
            console.log("Open modal for customer: " + customer.id);
            document.getElementById("customerId").value = customer.id;
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

// ....... This function handles given a SimpleEntity<User, Integer> (where user is a technician and integer is their jobCount) .......
// function handleDateChange(date) {
//   const techCheckboxDiv = document.getElementById("tech-checkbox");
//   techCheckboxDiv.innerHTML = ""; // Clear the div

//   fetch("/jobs/getJobsCount?date=" + date)
//     .then((response) => response.json())
//     .then((data) => {
//       data.forEach(item => {
//         // Extracting data from the json object
//         const userString = Object.keys(item)[0];

//         const idMatch = userString.match(/id=(\d+),/);
//         const techId = idMatch ? idMatch[1] : "Unknown";

//         const nameMatch = userString.match(/name='([^']+)'/);
//         const name = nameMatch ? nameMatch[1] : "Unknown";

//         const jobCount = item[userString];

//         const column = document.createElement("div");
//         column.classList.add("col-md-2");
//           const container = document.createElement("div");
//           container.classList.add("user-box");
//           container.classList.add("form-check");

//             const checkbox = document.createElement("input");
//             checkbox.id = `tech${techId}`;
//             checkbox.value = techId;
//             checkbox.name = "technicianIds";
//             checkbox.type = "checkbox";
//             checkbox.classList.add("form-check-input");
//             checkbox.addEventListener("change", function () { // Allows clicking on the user box to check it
//               if (this.checked) {
//                 checkbox.classList.add("checked");
//               } else {
//                 checkbox.classList.remove("checked");
//               }
//             });
//             container.appendChild(checkbox);

//             const label = document.createElement("label");
//             label.htmlFor = `tech${techId}`;
//             label.classList.add("form-check-label");
//               const nameDiv = document.createElement("div");
//               nameDiv.textContent = name;
//               label.appendChild(nameDiv);

//               const jobCountDiv = document.createElement("div");
//               jobCountDiv.textContent = `Jobs: ${jobCount}`;
//               label.appendChild(jobCountDiv);
//             container.appendChild(label);
//           column.appendChild(container);
//         techCheckboxDiv.appendChild(column);
//       });
//     })
//     .catch((error) => {
//       console.error('Error:', error);
//     });

// }

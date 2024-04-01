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
          // If filter is not "requesting-app", add an empty cell for the actions column
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
        appendCell(row, "td", "", "", customer.lastServiced);
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

// Calendar clicking for job date.....................................................
function resetColumnBackgrounds() {
  document.querySelectorAll(".calendar-top, .calendar-col").forEach((col) => {
    col.style.backgroundColor = ""; // Reset background color of .calendar-top
  });
}

function changeColumnColor(date) {
  resetColumnBackgrounds();
  let top = document.querySelector(`.week-date[datetime="${date}"]`);
  if (top == null) return;
  top = top.parentElement;

  let column = document.querySelector(`.calendar-col[datetime="${date}"]`);

  if (top == null || column == null) return;

  top.style.backgroundColor = "#ebebeb";
  column.style.backgroundColor = "#f0f0f0";
}

// Selecting top and col to allow clicking on the calendar
document.querySelectorAll(".calendar-top").forEach((col) => {
  col.addEventListener("click", (event) => {
    let date = col.querySelector(".week-date").getAttribute("datetime");
    document.querySelector("#dateService").value = date;
    changeColumnColor(date);
  });
});

document.querySelectorAll(".calendar-col").forEach((col) => {
  col.addEventListener("click", (event) => {
    let date = col.getAttribute("datetime");
    document.querySelector("#dateService").value = date;
    changeColumnColor(date);
  });
});

// Event listener for .job elements
document.querySelectorAll(".job").forEach((job) => {
  job.addEventListener("click", (event) => {
    // Stop the event from bubbling up to the column listeners
    event.stopPropagation();

    // maybe add job click function here
  });
});

// Updates the calendar to display the week provided (overwrites the existing function in calendar.js)
function displayWeek(weekDates) {
  resetColumnBackgrounds();

  let weekOffset = document.querySelector(`#week-offset`);
  if (weekOffset === null) return;

  if (currentWeekOffset > 0)
    weekOffset.textContent = "Current week +" + currentWeekOffset;
  else if (currentWeekOffset < 0)
    weekOffset.textContent = "Current week -" + currentWeekOffset * -1;
  else weekOffset.textContent = "Current week";

  weekDates.forEach((weekDay, index) => {
    let dateString = formatDate(weekDay.date); // YYYY-MM-DD

    let topDateElement = document.querySelector(
      `.calendar-top:nth-child(${index + 1}) .week-date`
    );
    topDateElement.textContent = `${weekDay.month} ${weekDay.day}`;

    topDateElement.setAttribute("datetime", dateString);

    let dayColumn = document.querySelector(
      `.calendar-col:nth-child(${index + 1})`
    );
    if (dayColumn == null) return;
    dayColumn.setAttribute("datetime", dateString);
  });

  let chosenDate = document.querySelector("#dateService").value;
  if (chosenDate !== "") {
    changeColumnColor(chosenDate);
  }

  fetchJobsAndDisplay(weekDates);
}

// Calendar clicking for job date.....................................................

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
      console.log(jobCounts);
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

document.querySelectorAll(".calendar-col").forEach((col) => {
  col.addEventListener("click", (event) => {
    let date = col.getAttribute("datetime");
    document.querySelector("#dateService").value = date;
    handleDateChange(date);
  });
});

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

document.getElementById("filter").addEventListener("change", function () {
  var selectedFilter = this.value;
  console.log(selectedFilter);
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
        appendCell(row, "td", "", "", customer.notes);

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
  // document.querySelectorAll('.calendar-col').forEach(col => {
  //   col.style.backgroundColor = ""; // Reset background color of .calendar-col
  // });
}

function changeColumnColor(date) {
  resetColumnBackgrounds();
  let top = document.querySelector(
    `.week-date[datetime="${date}"]`
  ).parentElement;
  let column = document.querySelector(`.calendar-col[datetime="${date}"]`);

  top.style.backgroundColor = "#e0e0e0";
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

// Calendar clicking for job date.....................................................

// Enables clicking on user boxes to check them..................
let userBoxes = document.querySelectorAll(".user-box");
userBoxes.forEach(function (box) {
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
var checkboxes = document.querySelectorAll('input[name="selectedUsers"]');
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
  const usersDiv = document.getElementById("users");
  usersDiv.innerHTML = ""; // Clear the div

  const formattedDate = new Date(date).toISOString().split("T")[0]; // Format the date as 'yyyy-mm-dd'

  fetch("/jobs/getJobsCount?date=" + formattedDate) // Change the endpoint to match your backend endpoint
    .then((response) => response.json())
    .then((data) => {
      console.log(data);
      for (const userId of Object.keys(data)) {
        const userName = data[userId].name;
        const jobsCount = data[userId].jobsCount;

        const userDiv = document.createElement("div");
        userDiv.className = "col-md-2";

        const checkBoxDiv = document.createElement("div");
        checkBoxDiv.className = "user-box form-check";

        const input = document.createElement("input");
        input.id = "user" + userId;
        input.value = userId;
        input.name = "technicianIds";
        input.type = "checkbox";
        input.className = "form-check-input";

        const label = document.createElement("label");
        label.htmlFor = "user" + userId;
        label.className = "form-check-label";

        const span = document.createElement("span");
        span.textContent = userName;

        const small = document.createElement("small");
        small.className = "text-muted";
        small.textContent = jobsCount + " jobs today";
        label.appendChild(span);
        label.appendChild(small);
        checkBoxDiv.appendChild(input);
        checkBoxDiv.appendChild(label);
        userDiv.appendChild(checkBoxDiv);
        usersDiv.appendChild(userDiv);
      }
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
    console.log("the date is: " + date);
  });
});

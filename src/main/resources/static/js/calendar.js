// Calulates and returns the dates for the week baseDate is in
function getWeekDates(baseDate) {
  let weekDates = [];
  for (let i = 0; i < 7; i++) {
    let date = new Date(baseDate);
    date.setDate(date.getDate() - date.getDay() + i); // First loop this is Sunday, then Monday, etc.
    weekDates.push({
      day: date.getDate(),
      month: date.toLocaleDateString("en-CA", { month: "short" }), // 'Jan', 'Feb', etc.
      year: date.getFullYear(),
      date: date,
    });
  }
  return weekDates;
}

function addCustomerNameAndDisplay(job, jobNameDiv, jobEntry, dayColumn) {
  fetch(`/customers/getCustomerNameFromId?customerId=${job.customerId}`)
    .then((response) => response.text())
    .then((name) => {
      jobNameDiv.textContent = name; // Adds the customer name to the jobName div
      dayColumn.appendChild(jobEntry); // Adds the job to its day column on calendar
    })
    .catch((error) => {
      console.error("Error fetching customer name:", error);
      dayColumn.appendChild(jobEntry);
    });
}

// Create a YYYY-MM-DD string based on local time, not UTC
function formatDate(date) {
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, "0"); // getMonth() is zero-indexed
  const day = date.getDate().toString().padStart(2, "0");
  return `${year}-${month}-${day}`;
}

// Converts from string date, which would otherwise be interpereted as UTC, to a Date object
function createDateFromString(dateString) {
  const parts = dateString.split("-");
  const year = parseInt(parts[0], 10);
  const month = parseInt(parts[1], 10) - 1; // Months are 0-indexed
  const day = parseInt(parts[2], 10);
  return new Date(year, month, day);
}

// Fetches jobs for the days inside weekDates and displays them on the calendar
function fetchJobsAndDisplay(weekDates) {
  let week = new Array(7);
  for (let i = 0; i < 7; i++) {
    week[i] = formatDate(weekDates[i].date);
  }

  const startDate = week[0];
  const endDate = week[6];

  fetch(`/jobs/getWeek?startDate=${startDate}&endDate=${endDate}`)
    .then((response) => response.json())
    .then((jobs) => {
      // Clear the current displayed jobs
      document.querySelectorAll(".job").forEach((job) => job.remove());

      // Display the jobs on the calendar
      jobs.forEach((job) => {
        let jobDate = createDateFromString(job.serviceDate);
        jobDate = formatDate(jobDate);
        let dayColumn;
        // Finds the column for the job
        for (let i = 0; i < 7; i++) {
          if (jobDate === week[i]) {
            dayColumn = document.querySelector(
              `.calendar-col:nth-child(${i + 1})`
            );
            break;
          }
        }

        // Make the div for the job
        let jobEntry = document.createElement("div");
        jobEntry.classList.add("job");
        if (job.jobDone) jobEntry.classList.add("finished");

        let jobNameDiv = document.createElement("div");
        jobNameDiv.classList.add("job-name");

        let jobTypeDiv = document.createElement("div");
        jobTypeDiv.classList.add("job-type");
        jobTypeDiv.textContent = job.jobType;

        // Append the job name/type divs to the job div
        jobEntry.appendChild(jobNameDiv);
        jobEntry.appendChild(jobTypeDiv);

        // Fetching is customer name is slow, so div doesnt get added to calendar until customer name is fetched
        addCustomerNameAndDisplay(job, jobNameDiv, jobEntry, dayColumn);
      });
    })
    .catch((error) => console.error("Error fetching jobs:", error));
}

// Updates the calendar to display the week provided
function displayWeek(weekDates) {
  let weekOffset = document.querySelector(`#week-offset`);
  if (weekOffset === null) return;

  if (currentWeekOffset > 0)
    weekOffset.textContent = "Current week +" + currentWeekOffset;
  else if (currentWeekOffset < 0)
    weekOffset.textContent = "Current week -" + currentWeekOffset * -1;
  else weekOffset.textContent = "Current week";

  weekDates.forEach((date, index) => {
    let dateSpan = document.querySelector(
      `.calendar-top:nth-child(${index + 1}) .week-date`
    );
    dateSpan.textContent = `${date.month} ${date.day}`;
  });

  fetchJobsAndDisplay(weekDates).then(() => {
    changeJobTypeColor();
  });
}

// Changes the week displayed on the calendar
function changeWeek(weeksToAdd) {
  currentWeekOffset += weeksToAdd;
  currentBaseDate.setDate(currentBaseDate.getDate() + weeksToAdd * 7);
  let weekDates = getWeekDates(currentBaseDate);
  displayWeek(weekDates);
}

// Initialize with current date
let currentBaseDate = new Date();
let currentWeekOffset = 0;
let currentWeekDates = getWeekDates(currentBaseDate);

//change background colour of div class job depending on job type: service, repair, install
function changeJobTypeColor() {
  let jobType = document.querySelectorAll(".job-type");
  jobType.forEach((job) => {
    if (job.textContent === "Service")
      job.parentElement.style.backgroundColor = "lightgreen";
    else if (job.textContent === "Repair")
      job.parentElement.style.backgroundColor = "lightcoral";
    else if (job.textContent === "Install")
      job.parentElement.style.backgroundColor = "lightblue";
  });
}

document.addEventListener("DOMContentLoaded", () => {
  // Display the current week
  displayWeek(currentWeekDates);
  changeJobTypeColor();
  console.log("changed job type color");
});

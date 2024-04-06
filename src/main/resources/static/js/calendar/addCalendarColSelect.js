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
      handleDateChange(date);
    });
  });
  
  document.querySelectorAll(".calendar-col").forEach((col) => {
    col.addEventListener("click", (event) => {
      let date = col.getAttribute("datetime");
      document.querySelector("#dateService").value = date;
      changeColumnColor(date);
      handleDateChange(date);
    });
  });
  
  // // Event listener for .job elements
  // document.querySelectorAll(".job").forEach((job) => {
  //   job.addEventListener("click", (event) => {
  //     // Stop the event from bubbling up to the column listeners
  //     event.stopPropagation();
  
  //     // maybe add job click function here
  //   });
  // });
  
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
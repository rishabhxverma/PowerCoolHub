function getWeekDates(baseDate) {
    let week = [];
    for (let i = 0; i < 7; i++) {
      let date = new Date(baseDate);
      date.setDate(date.getDate() - date.getDay() + i); // First loop this is Sunday, then Monday, etc.
      week.push({
        date: date.getDate(),
        month: date.toLocaleDateString('en-CA', { month: 'short' }) // 'Jan', 'Feb', etc.
      });
    }
    return week;
}

function displayWeek(weekDates) {
    let weekOffset = document.querySelector(`#week-offset`);
    if(currentWeekOffset > 0)
        weekOffset.textContent = "Current week +" + currentWeekOffset;
    else if( currentWeekOffset < 0)
        weekOffset.textContent = "Current week -" + (currentWeekOffset * -1);
    else
        weekOffset.textContent = "Current week";
    
    weekDates.forEach((date, index) => {
        let dateSpan = document.querySelector(`.calendar-top:nth-child(${index + 1}) .week-date`);
        dateSpan.textContent = `${date.month} ${date.date}`;
    });
}

function changeWeek(weeksToAdd) {
    currentWeekOffset += weeksToAdd;
    currentBaseDate.setDate(currentBaseDate.getDate() + (weeksToAdd * 7));
    let weekDates = getWeekDates(currentBaseDate);
    displayWeek(weekDates);
}

// Initialize with current date
let currentBaseDate = new Date();
let currentWeekOffset = 0;

// Display the current week
let currentWeekDates = getWeekDates(currentBaseDate);
displayWeek(currentWeekDates);
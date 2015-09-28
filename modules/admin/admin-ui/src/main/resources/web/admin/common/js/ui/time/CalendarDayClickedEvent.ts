module api.ui.time {

    export class CalendarDayClickedEvent {

        private calendarDay: CalendarDay;

        constructor(calendarDay: CalendarDay) {
            this.calendarDay = calendarDay;
        }

        getCalendarDay(): CalendarDay {
            return this.calendarDay;
        }
    }


}

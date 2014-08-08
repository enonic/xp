module api.ui.time {

    export class CalendarBuilder {

        year: number;

        monthOfYear: number;

        startingDayOfWeek: DayOfWeek = DaysOfWeek.MONDAY;

        setYear(value: number): CalendarBuilder {
            this.year = value;
            return this;
        }

        setMonthOfYear(value: number): CalendarBuilder {
            this.monthOfYear = value;
            return this;
        }

        setStartingDayOfWeek(value: DayOfWeek): CalendarBuilder {
            this.startingDayOfWeek = value;
            return this;
        }

        build(): Calendar {
            return new Calendar(this);
        }
    }

    export class Calendar extends api.dom.DivEl {

        private year: number;

        private monthOfYear: number;

        private calendarDays: CalendarDay[];

        private startingDayOfWeek: DayOfWeek;

        private weeks: CalendarWeek [];

        constructor(builder: CalendarBuilder) {
            super("calendar");

            this.year = builder.year;
            this.monthOfYear = builder.monthOfYear;
            this.startingDayOfWeek = builder.startingDayOfWeek;

            this.calendarDays = [];
            var daysInMonth = new Date(this.year, this.monthOfYear, 0).getDate();
            for (var i = 1; i <= daysInMonth; i++) {
                var calendarDay = this.createCalendarDay(i);
                this.calendarDays.push(calendarDay);
            }

            var firstDay: CalendarDay = null;
            if (this.startingDayOfWeek.equals(this.calendarDays[0].getDayOfWeek())) {
                firstDay = this.calendarDays[0];
            }
            else {
                var previousDay = this.calendarDays[0].getPrevious();
                while (!previousDay.getDayOfWeek().equals(this.startingDayOfWeek)) {
                    previousDay = previousDay.getPrevious();
                }
                firstDay = previousDay;
            }
            this.weeks = [];
            var currWeek = this.createCalendarWeek(firstDay);
            this.weeks.push(currWeek);
            this.appendChild(currWeek);
            while (!currWeek.hasLastDayOfMonth(this.monthOfYear)) {
                var newWeek = this.createCalendarWeek(currWeek.getNextWeeksFirstDay());
                this.weeks.push(newWeek);
                this.appendChild(newWeek);
                currWeek = newWeek;
            }
        }

        private createCalendarWeek(firstDayOfWeek: CalendarDay): CalendarWeek {
            var weekBuilder = new CalendarWeekBuilder();
            weekBuilder.addDay(firstDayOfWeek);
            var nextDay = firstDayOfWeek.getNext();
            for (var i = 0; i < 6; i++) {
                weekBuilder.addDay(nextDay);
                nextDay = nextDay.getNext();
            }
            return weekBuilder.build();
        }

        private createCalendarDay(dayOfMonth: number): CalendarDay {

            var date = new Date(this.year, this.monthOfYear, dayOfMonth);
            return new CalendarDay(date, this.monthOfYear);
        }

        public getCalendarDays(): CalendarDay [] {
            return this.calendarDays;
        }
    }
}

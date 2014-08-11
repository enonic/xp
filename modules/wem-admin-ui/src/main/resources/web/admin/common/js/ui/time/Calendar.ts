module api.ui.time {

    export class CalendarBuilder {

        year: number;

        monthOfYear: number;

        selectedDay: number = 0;

        startingDayOfWeek: DayOfWeek = DaysOfWeek.MONDAY;

        interactive: boolean = false;

        setYear(value: number): CalendarBuilder {
            this.year = value;
            return this;
        }

        setMonthOfYear(value: number): CalendarBuilder {
            this.monthOfYear = value;
            return this;
        }

        setSelectedDay(value: number): CalendarBuilder {
            this.selectedDay = value;
            return this;
        }

        setStartingDayOfWeek(value: DayOfWeek): CalendarBuilder {
            this.startingDayOfWeek = value;
            return this;
        }

        setInteractive(value: boolean): CalendarBuilder {
            this.interactive = value;
            return this;
        }

        build(): Calendar {
            return new Calendar(this);
        }
    }

    export class Calendar extends api.dom.DivEl {

        private interactive: boolean;

        private year: number;

        private monthOfYear: number;

        private selectedDay: number;

        private calendarDays: CalendarDay[];

        private startingDayOfWeek: DayOfWeek;

        private weeks: CalendarWeek [];

        private selectedDateChangedListeners: {(event: SelectedDateChangedEvent) : void}[] = [];

        constructor(builder: CalendarBuilder) {
            super("calendar");

            this.year = builder.year;
            this.monthOfYear = builder.monthOfYear;
            this.selectedDay = builder.selectedDay;
            this.startingDayOfWeek = builder.startingDayOfWeek;
            this.interactive = builder.interactive;

            this.calendarDays = this.resolveDaysInMonth();
            var firstDay = this.resolveFirstDayOfCalendar();
            this.weeks = this.createCalendarWeeks(firstDay);
            this.weeks.forEach((week) => {
                this.appendChild(week);
            });
        }

        private resolveDaysInMonth() {
            var calendarDays: CalendarDay[] = [];
            var daysInMonth = new Date(this.year, this.monthOfYear, 0).getDate();
            var previousDay: CalendarDay = null;
            for (var i = 1; i <= daysInMonth; i++) {
                var calendarDay = this.createCalendarDay(i, previousDay);
                calendarDays.push(calendarDay);
                previousDay = calendarDay;
            }
            return calendarDays;
        }

        private resolveFirstDayOfCalendar() {
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
            return firstDay;
        }

        private createCalendarWeeks(firstDay: CalendarDay) {
            var weeks: CalendarWeek [] = [];
            var currWeek = this.createCalendarWeek(firstDay);
            weeks.push(currWeek);
            while (!currWeek.hasLastDayOfMonth(this.monthOfYear)) {
                var newWeek = this.createCalendarWeek(currWeek.getNextWeeksFirstDay());
                weeks.push(newWeek);
                currWeek = newWeek;
            }
            return weeks;
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

        private createCalendarDay(dayOfMonth: number, previousDay: CalendarDay): CalendarDay {

            var date = new Date(this.year, this.monthOfYear, dayOfMonth);
            var calendarDay = new CalendarDayBuilder().
                setDate(date).
                setMonth(this.monthOfYear).
                setPreviousDay(previousDay).
                build();
            if (calendarDay.isInMonth()) {

                if (this.interactive) {
                    calendarDay.onCalendarDayClicked((event: CalendarDayClickedEvent) => {
                        this.handleCalendarDayClicked(event);
                    });
                }

                if (this.selectedDay == date.getDate()) {
                    calendarDay.setSelectedDay(true);
                }
            }
            return  calendarDay;
        }

        private handleCalendarDayClicked(event: CalendarDayClickedEvent) {

            this.calendarDays.forEach((calendarDay: CalendarDay) => {
                if (!event.getCalendarDay().equals(calendarDay)) {
                    calendarDay.setSelectedDay(false);
                }
            });
            event.getCalendarDay().setSelectedDay(true);
            this.selectedDay = event.getCalendarDay().getDayOfMonth();
            this.notifySelectedDateChanged(event.getCalendarDay().getDate());
        }

        public getSelectedDay(): number {
            return this.getSelectedDay();
        }

        public getCalendarDays(): CalendarDay [] {
            return this.calendarDays;
        }

        onSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.selectedDateChangedListeners.push(listener);
        }

        unSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.selectedDateChangedListeners = this.selectedDateChangedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifySelectedDateChanged(date: Date) {
            var event = new SelectedDateChangedEvent(date);
            this.selectedDateChangedListeners.forEach((listener) => {
                listener(event);
            });
        }
    }
}

module api.ui.time {

    export class CalendarBuilder {

        year: number;

        month: number;

        selectedDate: Date;

        startingDayOfWeek: DayOfWeek;

        interactive: boolean = false;

        setYear(value: number): CalendarBuilder {
            this.year = value;
            return this;
        }

        setMonth(value: number): CalendarBuilder {
            this.month = value;
            return this;
        }

        setSelectedDate(value: Date): CalendarBuilder {
            this.selectedDate = value;
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

        private month: number;

        private selectedDate: Date;

        private calendarDays: CalendarDay[];

        private startingDayOfWeek: DayOfWeek;

        private weeks: CalendarWeek [];

        private selectedDateChangedListeners: {(event: SelectedDateChangedEvent) : void}[] = [];

        private shownMonthChangedListeners: {(month: number, year: number) : void}[] = [];

        constructor(builder: CalendarBuilder) {
            super("calendar");

            var now = new Date();
            this.year = builder.year || now.getUTCFullYear();
            this.month = builder.month != undefined ? builder.month : now.getUTCMonth();
            this.selectedDate = builder.selectedDate;
            this.startingDayOfWeek = builder.startingDayOfWeek || DaysOfWeek.MONDAY;
            this.interactive = builder.interactive;

            this.renderMonth();
        }

        public selectDate(value: Date) {
            if (value) {
                this.year = value.getUTCFullYear();
                this.month = value.getUTCMonth();
                this.selectedDate = value;
                this.removeChildren();

                if (api.util.DateHelper.isInvalidDate(value)) {
                    var spanEl = new api.dom.SpanEl().setHtml("Invalid date");
                    this.appendChild(spanEl);
                }
                else {
                    this.renderMonth();
                }
            }
            else {
                this.selectedDate = null;
                var now = new Date();
                this.year = now.getUTCFullYear();
                this.month = now.getUTCMonth();
            }
        }

        public nextMonth() {
            this.month++;
            if (this.month > 11) {
                this.month = 0;
                this.year++;
            }
            this.removeChildren();
            this.renderMonth();
        }

        public previousMonth() {
            this.month--;
            if (this.month < 0) {
                this.month = 11;
                this.year--;
            }
            this.removeChildren();
            this.renderMonth();
        }

        public nextYear() {
            this.year++;
            this.removeChildren();
            this.renderMonth();
        }

        public previousYear() {
            this.year--;
            this.removeChildren();
            this.renderMonth();
        }

        private renderMonth() {
            this.calendarDays = this.resolveDaysInMonth();
            var firstDay = this.resolveFirstDayOfCalendar();
            this.weeks = this.createCalendarWeeks(firstDay);
            this.weeks.forEach((week) => {
                this.appendChild(week);
            });
            this.notifyShownMonthChanged(this.month, this.year);
        }

        private resolveDaysInMonth() {
            var calendarDays: CalendarDay[] = [];
            var daysInMonth = api.util.DateHelper.newUTCDate(this.year, this.month, 0).getDate();
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
            while (!currWeek.hasLastDayOfMonth(this.month)) {
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

            var date = api.util.DateHelper.newUTCDate(this.year, this.month, dayOfMonth);
            var calendarDay = new CalendarDayBuilder().
                setDate(date).
                setMonth(this.month).
                setPreviousDay(previousDay).
                build();
            if (calendarDay.isInMonth()) {

                if (this.interactive) {
                    calendarDay.onCalendarDayClicked((event: CalendarDayClickedEvent) => this.handleCalendarDayClicked(event));
                }

                if (this.selectedDate && this.selectedDate.toDateString() == date.toDateString()) {
                    calendarDay.setSelectedDay(true);
                }
            }
            return calendarDay;
        }

        private handleCalendarDayClicked(event: CalendarDayClickedEvent) {

            this.calendarDays.forEach((calendarDay: CalendarDay) => {
                calendarDay.setSelectedDay(event.getCalendarDay().equals(calendarDay));
            });

            this.selectedDate = event.getCalendarDay().getDate();
            this.notifySelectedDateChanged(this.selectedDate);
        }

        public getSelectedDate(): Date {
            return this.selectedDate;
        }

        public getMonth(): number {
            return this.month;
        }

        public getYear(): number {
            return this.year;
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
            });
        }

        private notifySelectedDateChanged(date: Date) {
            var event = new SelectedDateChangedEvent(date);
            this.selectedDateChangedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onShownMonthChanged(listener: (month: number, year: number) => void) {
            this.shownMonthChangedListeners.push(listener);
        }

        unShownMonthChanged(listener: (month: number, year: number) => void) {
            this.shownMonthChangedListeners = this.shownMonthChangedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyShownMonthChanged(month: number, year: number) {
            this.shownMonthChangedListeners.forEach((listener) => {
                listener(month, year);
            });
        }
    }
}

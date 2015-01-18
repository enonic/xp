module api.ui.time {

    export class CalendarDayBuilder {

        date: Date;

        month: number;

        previousDay: CalendarDay;

        nextDay: CalendarDay;

        setDate(value: Date): CalendarDayBuilder {
            this.date = value;
            return this;
        }

        setMonth(value: number): CalendarDayBuilder {
            this.month = value;
            return this;
        }

        setPreviousDay(value: CalendarDay): CalendarDayBuilder {
            this.previousDay = value;
            return this;
        }

        setNextDay(value: CalendarDay): CalendarDayBuilder {
            this.nextDay = value;
            return this;
        }

        build(): CalendarDay {
            return new CalendarDay(this);
        }
    }

    export class CalendarDay extends api.dom.LiEl implements api.Equitable {

        private date: Date;

        private month: number;

        private dayOfWeek: DayOfWeek;

        private previousDay: CalendarDay;

        private nextDay: CalendarDay;

        private selectedDay: boolean = false;

        private calendarDayClickedListeners: {(event: CalendarDayClickedEvent) : void}[] = [];

        constructor(builder: CalendarDayBuilder) {
            super("calendar-day");

            this.date = builder.date;
            this.month = builder.month;
            this.previousDay = builder.previousDay;
            if (this.previousDay) {
                this.previousDay.nextDay = this;
            }
            this.nextDay = builder.nextDay;
            if (this.nextDay) {
                this.nextDay.previousDay = this;
            }

            if (new Date().toDateString() == builder.date.toDateString()) {
                this.addClass('today');
            }

            this.dayOfWeek = DaysOfWeek.getByNumberCode(this.date.getDay());
            this.setHtml("" + this.date.getDate());

            if (this.isBeforeMonth()) {
                this.addClass("before-month");
            }

            if (this.isAfterMonth()) {
                this.addClass("after-month");
            }

            this.onClicked((event: MouseEvent) => {
                this.notifyCalendarDayClicked();
            });
        }

        getDate(): Date {
            return this.date;
        }

        getDayOfMonth(): number {
            return this.date.getDate();
        }

        setSelectedDay(value: boolean) {
            this.selectedDay = value;
            this.refreshSelectedDay();
        }

        refreshSelectedDay() {
            if (this.selectedDay && !this.hasClass("selected-day")) {
                this.addClass("selected-day");
            }
            else if (!this.selectedDay && this.hasClass("selected-day")) {
                this.removeClass("selected-day");
            }
        }

        isInMonth(): boolean {
            return !this.isBeforeMonth() || !this.isAfterMonth();
        }

        isBeforeMonth(): boolean {
            return this.date.getMonth() < this.month || (this.month == 0 && this.date.getMonth() == 11);
        }

        isAfterMonth(): boolean {
            return this.date.getMonth() > this.month || (this.month == 11 && this.date.getMonth() == 0);
        }

        isLastDayOfMonth(month: number): boolean {
            var lastDateOfMonth = api.util.DateHelper.newUTCDate(this.date.getFullYear(), month + 1, 0);
            return month == this.date.getMonth() && this.date.getDate() == lastDateOfMonth.getDate();
        }

        getDayOfWeek(): DayOfWeek {
            return this.dayOfWeek;
        }

        getPrevious(): CalendarDay {
            if (this.previousDay) {
                return this.previousDay;
            }
            else {
                var prevDate = api.util.DateHelper.newUTCDate(this.date.getFullYear(), this.date.getMonth(), this.date.getDate() - 1);
                this.previousDay = new CalendarDayBuilder().
                    setDate(prevDate).
                    setMonth(this.month).
                    setNextDay(this).
                    build();
                return this.previousDay;
            }
        }

        getNext(): CalendarDay {
            if (this.nextDay) {
                return this.nextDay;
            }
            else {
                var nextDate = api.util.DateHelper.newUTCDate(this.date.getFullYear(), this.date.getMonth(), this.date.getDate() + 1);
                this.nextDay = new CalendarDayBuilder().
                    setDate(nextDate).
                    setMonth(this.month).
                    setPreviousDay(this).
                    build();
                return this.nextDay;
            }
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, CalendarDay)) {
                return false;
            }

            var other = <CalendarDay>o;

            if (!api.ObjectHelper.dateEquals(this.date, other.date)) {
                return false;
            }

            if (!api.ObjectHelper.numberEquals(this.month, other.month)) {
                return false;
            }

            return true;
        }

        onCalendarDayClicked(listener: (event: CalendarDayClickedEvent) => void) {
            this.calendarDayClickedListeners.push(listener);
        }

        unCalendarDayClicked(listener: (event: CalendarDayClickedEvent) => void) {
            this.calendarDayClickedListeners = this.calendarDayClickedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyCalendarDayClicked() {
            var event = new CalendarDayClickedEvent(this);
            this.calendarDayClickedListeners.forEach((listener) => {
                listener(event);
            });
        }
    }
}

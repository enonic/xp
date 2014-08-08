module api.ui.time {

    export class CalendarDay extends api.dom.DivEl {

        private date: Date;

        private month: number;

        private dayOfWeek: DayOfWeek;

        constructor(date: Date, month: number) {
            this.date = date;
            this.month = month;
            this.dayOfWeek = DaysOfWeek.getByNumberCode(date.getDay());
            super("calendar-day");

            this.getEl().setInnerHtml("" + date.getDate());

            if (this.isBeforeMonth()) {
                this.addClass("before-month");
            }

            if (this.isAfterMonth()) {
                this.addClass("after-month");
            }
        }

        isBeforeMonth(): boolean {
            return this.date.getMonth() < this.month || (this.month == 0 && this.date.getMonth() == 11);
        }

        isAfterMonth(): boolean {
            return this.date.getMonth() > this.month || (this.month == 11 && this.date.getMonth() == 0);
        }

        isLastDayOfMonth(month: number): boolean {
            var lastDateOfMonth = new Date(this.date.getFullYear(), month + 1, 0);
            return month == this.date.getMonth() && this.date.getDate() == lastDateOfMonth.getDate();
        }

        getDayOfWeek(): DayOfWeek {
            return this.dayOfWeek;
        }

        getPrevious(): CalendarDay {
            var prevDate = new Date(this.date.getFullYear(), this.date.getMonth(), this.date.getDate() - 1);
            return new CalendarDay(prevDate, this.month);
        }

        getNext(): CalendarDay {
            var nextDate = new Date(this.date.getFullYear(), this.date.getMonth(), this.date.getDate() + 1);
            return new CalendarDay(nextDate, this.month);
        }
    }
}

module api.ui.time {

    export class DatePickerPopupBuilder {

        calendar: Calendar;

        closeOnOutsideClick: boolean = true;

        setCalendar(value: Calendar): DatePickerPopupBuilder {
            this.calendar = value;
            return this;
        }

        getCalendar(): Calendar {
            return this.calendar;
        }

        setCloseOnOutsideClick(value: boolean): DatePickerPopupBuilder {
            this.closeOnOutsideClick = value;
            return this;
        }

        isCloseOnOutsideClick(): boolean {
            return this.closeOnOutsideClick;
        }

        build(): DatePickerPopup {
            return new DatePickerPopup(this);
        }

    }

    export class DatePickerPopup extends api.dom.DivEl {

        private prevYear: api.dom.AEl;
        private year: api.dom.SpanEl;
        private nextYear: api.dom.AEl;
        private prevMonth: api.dom.AEl;
        private month: api.dom.SpanEl;
        private nextMonth: api.dom.AEl;
        private calendar: Calendar;

        constructor(builder: DatePickerPopupBuilder) {
            super('date-picker-dialog');

            var yearContainer = new api.dom.H2El('year-container');
            this.appendChild(yearContainer);

            this.prevYear = new api.dom.AEl('prev');
            this.prevYear.onClicked((e: MouseEvent) => {
                this.calendar.previousYear();
            });
            yearContainer.appendChild(this.prevYear);

            this.year = new api.dom.SpanEl();
            yearContainer.appendChild(this.year);

            this.nextYear = new api.dom.AEl('next');
            this.nextYear.onClicked((e: MouseEvent) => {
                this.calendar.nextYear();
            });
            yearContainer.appendChild(this.nextYear);

            var monthContainer = new api.dom.H5El('month-container');
            this.appendChild(monthContainer);

            this.prevMonth = new api.dom.AEl('prev');
            this.prevMonth.onClicked((e: MouseEvent) => {
                this.calendar.previousMonth();
            });
            monthContainer.appendChild(this.prevMonth);

            this.month = new api.dom.SpanEl();
            monthContainer.appendChild(this.month);

            this.nextMonth = new api.dom.AEl('next');
            this.nextMonth.onClicked((e: MouseEvent) => {
                this.calendar.nextMonth();
            });
            monthContainer.appendChild(this.nextMonth);

            this.calendar = builder.getCalendar() || new CalendarBuilder().build();

            this.year.setHtml(this.calendar.getYear().toString());
            this.month.setHtml(MonthsOfYear.getByNumberCode(this.calendar.getMonth()).getFullName());

            this.calendar.onShownMonthChanged((month: number, year: number) => {
                this.month.setHtml(MonthsOfYear.getByNumberCode(month).getFullName());
                this.year.setHtml(year.toString());
            });
            this.appendChild(this.calendar);

            if (builder.isCloseOnOutsideClick()) {
                api.dom.Body.get().onClicked((e: MouseEvent) => this.outsideClickListener(e));
            }
        }

        getSelectedDate(): Date {
            return this.calendar.getSelectedDate();
        }

        setSelectedDate(date: Date) {
            this.calendar.selectDate(date);
        }

        getCalendar(): Calendar {
            return this.calendar;
        }

        onSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.calendar.onSelectedDateChanged(listener);
        }

        unSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.calendar.unSelectedDateChanged(listener);
        }

        private outsideClickListener(e: MouseEvent) {
            if (!this.getEl().contains(<HTMLElement> e.target)) {
                this.hide();
            }
        }
    }

}
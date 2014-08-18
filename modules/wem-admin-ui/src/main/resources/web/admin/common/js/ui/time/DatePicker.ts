module api.ui.time {

    export class DatePickerBuilder {

        year: number;

        month: number;

        selectedDate: Date;

        startingDayOfWeek: DayOfWeek = DaysOfWeek.MONDAY;

        closeOnSelect: boolean = true;

        closeOnOutsideClick: boolean = true;

        setYear(value: number): DatePickerBuilder {
            this.year = value;
            return this;
        }

        setMonth(value: number): DatePickerBuilder {
            this.month = value;
            return this;
        }

        setSelectedDate(value: Date): DatePickerBuilder {
            this.selectedDate = value;
            return this;
        }

        setStartingDayOfWeek(value: DayOfWeek): DatePickerBuilder {
            this.startingDayOfWeek = value;
            return this;
        }

        setCloseOnSelect(value: boolean): DatePickerBuilder {
            this.closeOnSelect = value;
            return this;
        }

        setcloseOnOutsideClick(value: boolean): DatePickerBuilder {
            this.closeOnOutsideClick = value;
            return this;
        }

        build(): DatePicker {
            return new DatePicker(this);
        }

    }

    export class DatePicker extends api.dom.DivEl {

        private dialog: DatePickerDialog;

        private input: api.ui.text.TextInput;

        private trigger: api.ui.button.Button;

        constructor(builder: DatePickerBuilder) {
            super('date-picker');

            this.input = api.ui.text.TextInput.middle();
            this.input.onClicked((e: MouseEvent) => {
                e.stopPropagation();
                e.preventDefault();

                this.dialog.show();
            });
            this.appendChild(this.input);

            this.trigger = new api.ui.button.Button('Change');
            this.appendChild(this.trigger);

            this.dialog = new DatePickerDialog(builder);
            this.appendChild(this.dialog);

            this.trigger.onClicked((e: MouseEvent) => {
                e.stopPropagation();
                e.preventDefault();

                if (this.dialog.isVisible()) {
                    this.dialog.hide();
                } else {
                    this.dialog.show();
                }
            });

            if (builder.selectedDate) {
                this.input.setValue(this.formatDate(builder.selectedDate));
            }

            this.onSelectedDateChanged((e: SelectedDateChangedEvent) => {
                if (builder.closeOnSelect) {
                    this.dialog.hide();
                }
                this.input.setValue(this.formatDate(e.getDate()));
            });

        }

        onSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.dialog.onSelectedDateChanged(listener);
        }

        unSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.dialog.unSelectedDateChanged(listener);
        }

        private formatDate(date: Date): string {
            return date.toDateString();
        }

    }

    class DatePickerDialog extends api.dom.DivEl {

        private prevYear: api.dom.AEl;
        private year: api.dom.SpanEl;
        private nextYear: api.dom.AEl;
        private prevMonth: api.dom.AEl;
        private month: api.dom.SpanEl;
        private nextMonth: api.dom.AEl;
        private calendar: Calendar;

        constructor(builder: DatePickerBuilder) {
            super('date-picker-dialog');

            var yearContainer = new api.dom.H2El('year-container');
            this.appendChild(yearContainer);

            this.prevYear = new api.dom.AEl('prev year');
            this.prevYear.onClicked((e: MouseEvent) => {
                this.calendar.previousYear();
            });
            yearContainer.appendChild(this.prevYear);

            this.year = new api.dom.SpanEl();
            yearContainer.appendChild(this.year);

            this.nextYear = new api.dom.AEl('next year');
            this.nextYear.onClicked((e: MouseEvent) => {
                this.calendar.nextYear();
            });
            yearContainer.appendChild(this.nextYear);

            var monthContainer = new api.dom.H5El('month-container');
            this.appendChild(monthContainer);

            this.prevMonth = new api.dom.AEl('prev month');
            this.prevMonth.onClicked((e: MouseEvent) => {
                this.calendar.previousMonth();
            });
            monthContainer.appendChild(this.prevMonth);

            this.month = new api.dom.SpanEl();
            monthContainer.appendChild(this.month);

            this.nextMonth = new api.dom.AEl('next month');
            this.nextMonth.onClicked((e: MouseEvent) => {
                this.calendar.nextMonth();
            });
            monthContainer.appendChild(this.nextMonth);

            this.calendar = new CalendarBuilder().
                setSelectedDate(builder.selectedDate).
                setMonth(builder.month).
                setYear(builder.year).
                setInteractive(true).
                build();

            this.year.setHtml(this.calendar.getYear().toString());
            this.month.setHtml(MonthsOfYear.getByNumberCode(this.calendar.getMonth()).getFullName());

            this.calendar.onShownMonthChanged((month: number, year: number) => {
                this.month.setHtml(MonthsOfYear.getByNumberCode(month).getFullName());
                this.year.setHtml(year.toString());
            });
            this.appendChild(this.calendar);

            if (builder.closeOnOutsideClick) {
                api.dom.Body.get().onClicked((e: MouseEvent) => this.outsideClickListener(e));
            }
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
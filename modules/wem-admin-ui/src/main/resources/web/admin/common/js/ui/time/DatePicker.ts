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

        setCloseOnOutsideClick(value: boolean): DatePickerBuilder {
            this.closeOnOutsideClick = value;
            return this;
        }

        build(): DatePicker {
            return new DatePicker(this);
        }

    }

    export class DatePicker extends api.dom.DivEl {

        private popup: DatePickerPopup;

        private input: api.ui.text.TextInput;

        private popupTrigger: api.ui.button.Button;

        private calendar: Calendar;

        private selectedDate: Date;

        private selectedDateChangedListeners: {(event: SelectedDateChangedEvent) : void}[] = [];

        constructor(builder: DatePickerBuilder) {
            super('date-picker');

            this.input = api.ui.text.TextInput.middle();
            this.input.onClicked((e: MouseEvent) => {
                e.stopPropagation();
                e.preventDefault();

                this.popup.show();
            });

            var wrapper = new api.dom.DivEl('wrapper');
            wrapper.appendChild(this.input);

            this.popupTrigger = new api.ui.button.Button();
            this.popupTrigger.addClass('icon-calendar4');
            this.appendChild(this.popupTrigger);

            this.calendar = new CalendarBuilder().
                setSelectedDate(builder.selectedDate).
                setMonth(builder.month).
                setYear(builder.year).
                setInteractive(true).
                build();

            this.popup = new DatePickerPopup(this.calendar, builder);
            wrapper.appendChild(this.popup);

            this.appendChild(wrapper);

            this.popupTrigger.onClicked((e: MouseEvent) => {
                e.stopPropagation();
                e.preventDefault();

                if (this.popup.isVisible()) {
                    this.popup.hide();
                } else {
                    this.popup.show();
                }
            });

            if (builder.selectedDate) {
                this.input.setValue(this.formatDate(builder.selectedDate));
                this.selectedDate = builder.selectedDate;
            }

            this.popup.onSelectedDateChanged((e: SelectedDateChangedEvent) => {
                if (builder.closeOnSelect) {
                    this.popup.hide();
                }
                this.selectedDate = e.getDate();
                this.input.setValue(this.formatDate(e.getDate()));
                this.notifySelectedDateChanged(e);
            });

            this.input.onKeyUp((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isNumber(event) ||
                    api.ui.KeyHelper.isDash(event) ||
                    api.ui.KeyHelper.isBackspace(event) ||
                    api.ui.KeyHelper.isDel(event)) {

                    var typedDate = this.input.getValue();
                    if (api.util.StringHelper.isEmpty(typedDate)) {
                        this.calendar.selectDate(null);
                        this.selectedDate = null;
                        this.popup.hide();
                        this.notifySelectedDateChanged(new SelectedDateChangedEvent(null));
                    }
                    else {
                        var date = api.util.DateHelper.parseUTCDate(typedDate);
                        if (date) {
                            var correctlyTyped = api.util.DateHelper.formatUTCDate(date) == typedDate;
                            if (correctlyTyped) {
                                this.selectedDate = date;
                                this.calendar.selectDate(date);
                                this.notifySelectedDateChanged(new SelectedDateChangedEvent(date));
                                if (!this.popup.isVisible()) {
                                    this.popup.show();
                                }
                            }
                            else {
                                this.selectedDate = null;
                                this.notifySelectedDateChanged(new SelectedDateChangedEvent(null));
                            }
                        }
                        else {
                            this.selectedDate = null;
                            this.notifySelectedDateChanged(new SelectedDateChangedEvent(null));
                        }
                    }
                }
            });

        }

        getSelectedDate(): Date {
            return this.selectedDate;
        }

        onSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.selectedDateChangedListeners.push(listener);
        }

        unSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.selectedDateChangedListeners = this.selectedDateChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifySelectedDateChanged(event: SelectedDateChangedEvent) {
            this.selectedDateChangedListeners.forEach((listener) => {
                listener(event);
            });
        }

        private formatDate(date: Date): string {
            return api.util.DateHelper.formatUTCDate(date);
        }

    }

    class DatePickerPopup extends api.dom.DivEl {

        private prevYear: api.dom.AEl;
        private year: api.dom.SpanEl;
        private nextYear: api.dom.AEl;
        private prevMonth: api.dom.AEl;
        private month: api.dom.SpanEl;
        private nextMonth: api.dom.AEl;
        private calendar: Calendar;

        constructor(calendar: Calendar, builder: DatePickerBuilder) {
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

            this.calendar = calendar;

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

        getSelectedDate(): Date {
            return this.calendar.getSelectedDate();
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
module api.ui.time {

    export class DateTimePickerBuilder {

        year: number;

        month: number;

        selectedDate: Date;

        hours: number;

        minutes: number;

        startingDayOfWeek: DayOfWeek = DaysOfWeek.MONDAY;

        closeOnSelect: boolean = true;

        closeOnOutsideClick: boolean = true;

        setYear(value: number): DateTimePickerBuilder {
            this.year = value;
            return this;
        }

        setMonth(value: number): DateTimePickerBuilder {
            this.month = value;
            return this;
        }

        setSelectedDate(value: Date): DateTimePickerBuilder {
            this.selectedDate = value;
            return this;
        }

        setHours(value: number): DateTimePickerBuilder {
            this.hours = value;
            return this;
        }

        setMinutes(value: number): DateTimePickerBuilder {
            this.minutes = value;
            return this;
        }

        setStartingDayOfWeek(value: DayOfWeek): DateTimePickerBuilder {
            this.startingDayOfWeek = value;
            return this;
        }

        setCloseOnSelect(value: boolean): DateTimePickerBuilder {
            this.closeOnSelect = value;
            return this;
        }

        setCloseOnOutsideClick(value: boolean): DateTimePickerBuilder {
            this.closeOnOutsideClick = value;
            return this;
        }

        build(): DateTimePicker {
            return new DateTimePicker(this);
        }

    }

    export class DateTimePicker extends api.dom.DivEl {

        private popup: DateTimePickerPopup;

        private input: api.ui.text.TextInput;

        private popupTrigger: api.ui.button.Button;

        private calendar: Calendar;

        private selectedDate: Date;

        constructor(builder: DateTimePickerBuilder) {
            super('date-time-picker');

            this.input = api.ui.text.TextInput.middle();
            this.input.onClicked((e: MouseEvent) => {
                e.stopPropagation();
                e.preventDefault();

                this.popup.show();
            });

            var wrapper = new api.dom.DivEl('wrapper');
            wrapper.appendChild(this.input);

            this.calendar = new CalendarBuilder().
                setSelectedDate(builder.selectedDate).
                setMonth(builder.month).
                setYear(builder.year).
                setInteractive(true).
                build();

            this.popup = new DateTimePickerPopup(this.calendar, builder);
            wrapper.appendChild(this.popup);

            this.popupTrigger = new api.ui.button.Button();
            this.popupTrigger.addClass('icon-calendar4');
            wrapper.appendChild(this.popupTrigger);

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

            if (builder.hours || builder.minutes) {
                this.input.setValue(this.formatTime(builder.hours, builder.minutes));
            }

            this.onSelectedDateChanged((e: SelectedDateChangedEvent) => {
                if (builder.closeOnSelect) {
                    this.popup.hide();
                }
                this.selectedDate = e.getDate();
                this.input.setValue(this.formatDate(e.getDate()) + " " +
                    this.formatTime(this.popup.getSelectedTime().hour, this.popup.getSelectedTime().minute));
            });

            this.onSelectedTimeChanged((hours: number, minutes: number) => {
                if (!this.popup.getSelectedDate()) {
                    this.selectedDate = new Date();
                    this.calendar.selectDate(this.selectedDate);
                }
                this.input.setValue(this.formatDate(this.popup.getSelectedDate()) + " " + this.formatTime(hours, minutes));
            });

            this.input.onKeyUp((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isNumber(event) ||
                    api.ui.KeyHelper.isDash(event) ||
                    api.ui.KeyHelper.isBackspace(event) ||
                    api.ui.KeyHelper.isDel(event)) {

                    var typedDateTime = this.input.getValue();
                    if (api.util.StringHelper.isEmpty(typedDateTime)) {
                        this.calendar.selectDate(null);
                        this.selectedDate = null;
                        this.popup.hide();
                    } else {
                        var date = api.util.DateHelper.parseUTCDateTime(typedDateTime);
                        if (date && date.toString() != "Invalid Date") {
                            this.selectedDate = date;
                            this.calendar.selectDate(date);
                            this.popup.setSelectedTime(date.getUTCHours(), date.getUTCMinutes());
                            if (!this.popup.isVisible()) {
                                this.popup.show();
                            }
                        } else {
                            this.selectedDate = null;
                        }
                    }
                }
            });

        }

        getSelectedDateTime(): Date {
            return this.popup.getSelectedDateTime();
        }

        getSelectedDate(): Date {
            return this.selectedDate;
        }

        onSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.popup.onSelectedDateChanged(listener);
        }

        unSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.popup.unSelectedDateChanged(listener);
        }

        private formatDate(date: Date): string {
            return api.util.DateHelper.formatUTCDate(date);
        }

        getSelectedTime(): {hour: number; minute: number} {
            return this.popup.getSelectedTime();
        }

        onSelectedTimeChanged(listener: (hours: number, minutes: number) => void) {
            this.popup.onSelectedTimeChanged(listener);
        }

        unSelectedTimeChanged(listener: (hours: number, minutes: number) => void) {
            this.popup.unSelectedTimeChanged(listener);
        }

        formatTime(hours: number, minutes: number): string {
            return this.padNumber(hours, 2) + ':' + this.padNumber(minutes, 2);
        }

        private padNumber(value: number, pad: number): string {
            return Array(pad - String(value).length + 1).join('0') + value;
        }

    }

    class DateTimePickerPopup extends api.dom.DivEl {

        private prevYear: api.dom.AEl;
        private year: api.dom.SpanEl;
        private nextYear: api.dom.AEl;
        private prevMonth: api.dom.AEl;
        private month: api.dom.SpanEl;
        private nextMonth: api.dom.AEl;
        private calendar: Calendar;

        private nextHour: api.dom.AEl;
        private hour: api.dom.SpanEl;
        private prevHour: api.dom.AEl;
        private nextMinute: api.dom.AEl;
        private minute: api.dom.SpanEl;
        private prevMinute: api.dom.AEl;

        private selectedHour: number;
        private selectedMinute: number;
        private interval: number;

        private timeChangedListeners: {(hours: number, minutes: number) : void}[] = [];

        constructor(calendar: Calendar, builder: DateTimePickerBuilder) {
            super('date-time-dialog');

            // Date
            var dateContainer = new api.dom.DivEl('date-picker-dialog');

            var yearContainer = new api.dom.H2El('year-container');
            dateContainer.appendChild(yearContainer);

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
            dateContainer.appendChild(monthContainer);

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
            dateContainer.appendChild(this.calendar);

            this.appendChild(dateContainer);


            // Time
            var timeContainer = new api.dom.DivEl('time-picker-dialog');

            var hourContainer = new api.dom.LiEl();
            timeContainer.appendChild(hourContainer);

            this.nextHour = new api.dom.AEl('next');
            this.nextHour.onMouseDown((e: MouseEvent) => {
                this.addHour(+1);
                this.startInterval(this.addHour, 1);
            });
            api.dom.Body.get().onMouseUp((e: MouseEvent) => {
                this.stopInterval();
            });
            this.nextHour.appendChild(new api.dom.SpanEl());
            hourContainer.appendChild(this.nextHour);

            this.hour = new api.dom.SpanEl();
            hourContainer.appendChild(this.hour);

            this.prevHour = new api.dom.AEl('prev');
            this.prevHour.onMouseDown((e: MouseEvent) => {
                this.addHour(-1);
                this.startInterval(this.addHour, -1)
            });
            this.prevHour.appendChild(new api.dom.SpanEl());
            hourContainer.appendChild(this.prevHour);

            timeContainer.appendChild(new api.dom.LiEl('colon'));

            var minuteContainer = new api.dom.LiEl();
            timeContainer.appendChild(minuteContainer);

            this.nextMinute = new api.dom.AEl('next');
            this.nextMinute.onMouseDown((e: MouseEvent) => {
                this.addMinute(+1);
                this.startInterval(this.addMinute, 1);
            });
            this.nextMinute.appendChild(new api.dom.SpanEl());
            minuteContainer.appendChild(this.nextMinute);

            this.minute = new api.dom.SpanEl();
            minuteContainer.appendChild(this.minute);

            this.prevMinute = new api.dom.AEl('prev');
            this.prevMinute.onMouseDown((e: MouseEvent) => {
                this.addMinute(-1);
                this.startInterval(this.addMinute, -1);
            });
            this.prevMinute.appendChild(new api.dom.SpanEl());
            minuteContainer.appendChild(this.prevMinute);

            this.selectedHour = builder.hours != undefined ? builder.hours : 0;
            this.selectedMinute = builder.minutes != undefined ? builder.minutes : 0;

            this.hour.setHtml(this.padNumber(this.selectedHour, 2));
            this.minute.setHtml(this.padNumber(this.selectedMinute, 2));

            this.appendChild(timeContainer);

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

        getSelectedTime(): {hour: number; minute: number} {
            return {
                hour: this.selectedHour,
                minute: this.selectedMinute
            }
        }

        onSelectedTimeChanged(listener: (hours: number, minutes: number) => void) {
            this.timeChangedListeners.push(listener);
        }

        unSelectedTimeChanged(listener: (hours: number, minutes: number) => void) {
            this.timeChangedListeners = this.timeChangedListeners.filter((curr) => {
                return curr != listener;
            });
        }

        getSelectedDateTime(): Date {
            var date = this.getSelectedDate();
            var time = this.getSelectedTime();
            if (!date || !time) {
                return null;
            }
            return api.util.DateHelper.newUTCDateTime(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(),
                time.hour, time.minute);
        }

        private startInterval(fn: Function, ...args: any[]) {
            var times = 0;
            var delay = 400;
            var intervalFn = () => {
                fn.apply(this, args);
                if (++times % 5 == 0 && delay > 50) {
                    // speed up after 5 occurrences but not faster than 50ms
                    this.stopInterval();
                    delay /= 2;
                    this.interval = setInterval(intervalFn, delay);
                }
            };
            this.interval = setInterval(intervalFn, delay);
        }

        private stopInterval() {
            clearInterval(this.interval);
        }

        setSelectedTime(hours: number, minutes: number, silent?: boolean) {
            this.selectedHour = Math.min(23, Math.max(0, hours));
            this.selectedMinute = Math.min(59, Math.max(0, minutes));

            this.hour.setHtml(this.padNumber(this.selectedHour, 2));
            this.minute.setHtml(this.padNumber(this.selectedMinute, 2));
            if (!silent) {
                this.notifyTimeChanged(this.selectedHour, this.selectedMinute);
            }
        }

        private addHour(add: number, silent?: boolean) {
            this.selectedHour += add;
            if (this.selectedHour < 0) {
                this.selectedHour += 24;
            } else if (this.selectedHour > 23) {
                this.selectedHour -= 24;
            }

            this.hour.setHtml(this.padNumber(this.selectedHour, 2));
            if (!silent) {
                this.notifyTimeChanged(this.selectedHour, this.selectedMinute);
            }
        }

        private addMinute(add: number, silent?: boolean) {
            this.selectedMinute += add;
            if (this.selectedMinute < 0) {
                this.selectedMinute += 60
                this.addHour(-1, true);
            } else if (this.selectedMinute > 59) {
                this.selectedMinute -= 60;
                this.addHour(1, true);
            }
            this.minute.setHtml(this.padNumber(this.selectedMinute, 2));
            if (!silent) {
                this.notifyTimeChanged(this.selectedHour, this.selectedMinute);
            }
        }

        private notifyTimeChanged(hours: number, minutes: number) {
            this.timeChangedListeners.forEach((listener) => {
                listener(hours, minutes);
            });
        }

        private padNumber(value: number, pad: number): string {
            return Array(pad - String(value).length + 1).join('0') + value;
        }
    }
}
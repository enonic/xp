module api.ui.time {

    export class DateTimePickerBuilder {

        year: number;

        month: number;

        selectedDate: Date;

        hours: number;

        minutes: number;

        startingDayOfWeek: DayOfWeek = DaysOfWeek.MONDAY;

        closeOnSelect: boolean = false;

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

        private validUserInput: boolean;

        private selectedDateTimeChangedListeners: {(event: SelectedDateChangedEvent) : void}[] = [];

        constructor(builder: DateTimePickerBuilder) {
            super('date-time-picker');

            this.validUserInput = true;
            this.input =
            api.ui.text.TextInput.middle();
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

            var popupBuilder = new DateTimePickerPopupBuilder().
                setHours(builder.hours).
                setMinutes(builder.minutes).
                setCalendar(this.calendar).
                setCloseOnOutsideClick(builder.closeOnOutsideClick);
            this.popup = new DateTimePickerPopup(popupBuilder);
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
                var value = this.input.getValue() || "";
                this.setTime(builder.hours, builder.minutes);
                this.input.setValue(value + " " + this.formatTime(builder.hours, builder.minutes));
            }

            this.popup.onSelectedDateChanged((e: SelectedDateChangedEvent) => {
                if (builder.closeOnSelect) {
                    this.popup.hide();
                }

                this.setDate(e.getDate());
                this.input.setValue(this.formatDate(this.selectedDate) + " " +
                                    this.formatTime(this.selectedDate.getUTCHours(), this.selectedDate.getUTCMinutes()));
                this.notifySelectedDateTimeChanged(new SelectedDateChangedEvent(this.selectedDate));
                this.validUserInput = true;
                this.updateInputStyling();
            });

            this.popup.onSelectedTimeChanged((hours: number, minutes: number) => {
                this.setTime(hours, minutes);
                this.input.setValue(this.formatDate(this.selectedDate) + " " + this.formatTime(hours, minutes));
                this.notifySelectedDateTimeChanged(new SelectedDateChangedEvent(this.selectedDate));
                this.validUserInput = true;
                this.updateInputStyling();
            });

            this.input.onKeyDown((event: KeyboardEvent) => {
                if (!api.ui.KeyHelper.isNumber(event) && !api.ui.KeyHelper.isDash(event) && !api.ui.KeyHelper.isBackspace(event) &&
                    !api.ui.KeyHelper.isDel(event) && !api.ui.KeyHelper.isColon(event)) {

                    event.preventDefault();
                }
            });

            this.input.onKeyUp((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isNumber(event) ||
                    api.ui.KeyHelper.isDash(event) ||
                    api.ui.KeyHelper.isBackspace(event) ||
                                                        api.ui.KeyHelper.isDel(event) ||
                                                        api.ui.KeyHelper.isColon(event)) {

                    var typedDateTime = this.input.getValue();
                    if (api.util.StringHelper.isEmpty(typedDateTime)) {
                        this.calendar.selectDate(null);
                        this.selectedDate = null;
                        this.validUserInput = true;
                        this.notifySelectedDateTimeChanged(new SelectedDateChangedEvent(null));
                        this.popup.hide();
                    } else {
                        var date = api.util.DateHelper.parseUTCDateTime(typedDateTime);
                        var dateLength = date.getUTCFullYear().toString().length + 12;
                        if (date && date.toString() != "Invalid Date" && typedDateTime.length == dateLength) {
                            this.selectedDate = date;
                            this.validUserInput = true;
                            this.calendar.selectDate(date);
                            this.popup.setSelectedTime(date.getUTCHours(), date.getUTCMinutes());
                            this.notifySelectedDateTimeChanged(new SelectedDateChangedEvent(date));
                            if (!this.popup.isVisible()) {
                                this.popup.show();
                            }
                        } else {
                            this.selectedDate = null;
                            this.validUserInput = false;
                            this.notifySelectedDateTimeChanged(new SelectedDateChangedEvent(null));
                        }
                    }

                    this.updateInputStyling();
                }
            });

        }

        private setTime(hours: number, minutes: number) {
            if (!this.selectedDate) {
                var today = new Date();
                this.selectedDate = api.util.DateHelper.newUTCDate(today.getUTCFullYear(), today.getUTCMonth(), today.getUTCDate());
            }
            this.selectedDate.setUTCHours(hours);
            this.selectedDate.setUTCMinutes(minutes);
        }

        private setDate(date: Date) {
            var hours = this.selectedDate ? this.selectedDate.getUTCHours() : 0,
                minutes = this.selectedDate ? this.selectedDate.getUTCMinutes() : 0;

            this.selectedDate = date;

            if (hours || minutes) {
                this.setTime(hours, minutes);
            }
        }

        hasValidUserInput(): boolean {
            return this.validUserInput;
        }

        getSelectedDateTime(): Date {
            return this.selectedDate;
        }

        onSelectedDateTimeChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.selectedDateTimeChangedListeners.push(listener);
        }

        unSelectedDateTimeChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.selectedDateTimeChangedListeners = this.selectedDateTimeChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifySelectedDateTimeChanged(event: SelectedDateChangedEvent) {
            this.selectedDateTimeChangedListeners.forEach((listener) => {
                listener(event);
            })
        }

        private formatDate(date: Date): string {
            return api.util.DateHelper.formatUTCDate(date);
        }

        private formatTime(hours: number, minutes: number): string {
            return this.padNumber(hours, 2) + ':' + this.padNumber(minutes, 2);
        }

        private padNumber(value: number, pad: number): string {
            return Array(pad - String(value).length + 1).join('0') + value;
        }

        private updateInputStyling() {
            this.input.updateValidationStatusOnUserInput(this.validUserInput);
        }

        giveFocus(): boolean {
            return this.input.giveFocus();
        }
    }

}
module api.ui.time {

    export class DatePickerBuilder {

        year: number;

        month: number;

        selectedDate: Date;

        calendar: Calendar;

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

        setCalendar(value: Calendar): DatePickerBuilder {
            this.calendar = value;
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

        private validUserInput: boolean;

        private selectedDateChangedListeners: {(event: SelectedDateChangedEvent) : void}[] = [];

        constructor(builder: DatePickerBuilder) {
            super('date-picker');
            this.validUserInput = true;

            this.input = api.ui.text.TextInput.middle();
            this.input.onClicked((e: MouseEvent) => {
                e.stopPropagation();
                e.preventDefault();

                this.popup.show();
            });

            var wrapper = new api.dom.DivEl('wrapper');
            wrapper.appendChild(this.input);

            this.calendar = builder.calendar || new CalendarBuilder().
                setSelectedDate(builder.selectedDate).
                setMonth(builder.month).
                setYear(builder.year).
                setInteractive(true).
                build();

            var popupBuilder = new DatePickerPopupBuilder().
                setCalendar(this.calendar).
                setCloseOnOutsideClick(builder.closeOnOutsideClick);
            this.popup = popupBuilder.build();
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

            this.popup.onSelectedDateChanged((e: SelectedDateChangedEvent) => {
                if (builder.closeOnSelect) {
                    this.popup.hide();
                }
                this.selectedDate = e.getDate();
                this.input.setValue(this.formatDate(e.getDate()));
                this.notifySelectedDateChanged(e);
                this.validUserInput = true;
                this.updateInputStyling();
            });

            /* may be added later
            this.input.onKeyDown((event: KeyboardEvent) => {

                if (!api.ui.KeyHelper.isNumber(event) && !api.ui.KeyHelper.isDash(event) && !api.ui.KeyHelper.isBackspace(event) &&
                    !api.ui.KeyHelper.isDel(event)) {

                    event.preventDefault();
                }
             });*/

            this.input.onKeyUp((event: KeyboardEvent) => {
                // may be added later
                //if (api.ui.KeyHelper.isNumber(event) ||
                //    api.ui.KeyHelper.isDash(event) ||
                //    api.ui.KeyHelper.isBackspace(event) ||
                //    api.ui.KeyHelper.isDel(event)) {

                    var typedDate = this.input.getValue();

                    if (api.util.StringHelper.isEmpty(typedDate)) {
                        this.calendar.selectDate(null);
                        this.selectedDate = null;
                        this.validUserInput = true;
                        this.popup.hide();
                        this.notifySelectedDateChanged(new SelectedDateChangedEvent(null));
                    }
                    else {
                        var date = api.util.DateHelper.parseUTCDate(typedDate);
                        if (date) {
                            var correctlyTyped = api.util.DateHelper.formatUTCDate(date) == typedDate;
                            if (correctlyTyped) {
                                this.selectedDate = date;
                                this.validUserInput = true;
                                this.calendar.selectDate(date);
                                this.notifySelectedDateChanged(new SelectedDateChangedEvent(date));
                                if (!this.popup.isVisible()) {
                                    this.popup.show();
                                }
                            }
                            else {
                                this.selectedDate = null;
                                this.validUserInput = false;
                                this.notifySelectedDateChanged(new SelectedDateChangedEvent(null));
                            }
                        }
                        else {
                            this.selectedDate = null;
                            this.validUserInput = false;
                            this.notifySelectedDateChanged(new SelectedDateChangedEvent(null));
                        }
                    }

                    this.updateInputStyling();

                //}
            });

        }

        hasValidUserInput(): boolean {
            return this.validUserInput;
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

        private updateInputStyling() {
            this.input.updateValidationStatusOnUserInput(this.validUserInput);
        }

    }
}
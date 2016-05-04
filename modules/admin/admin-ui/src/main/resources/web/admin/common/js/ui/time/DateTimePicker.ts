module api.ui.time {

    import Timezone = api.util.Timezone;

    export class DateTimePickerBuilder {

        year: number;

        month: number;

        selectedDate: Date;

        hours: number;

        minutes: number;

        startingDayOfWeek: DayOfWeek = DaysOfWeek.MONDAY;

        closeOnSelect: boolean = false;

        closeOnOutsideClick: boolean = true;

        timezone: Timezone;

        // use local timezone if timezone value is not initialized
        useLocalTimezoneIfNotPresent: boolean = false;

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

        setTimezone(value: Timezone): DateTimePickerBuilder {
            this.timezone = value;
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

        setUseLocalTimezoneIfNotPresent(value: boolean): DateTimePickerBuilder {
            this.useLocalTimezoneIfNotPresent = value;
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

        private selectedDate: Date;

        private timezone: Timezone;

        private validUserInput: boolean;

        private selectedDateTimeChangedListeners: {(event: SelectedDateChangedEvent) : void}[] = [];

        constructor(builder: DateTimePickerBuilder) {
            super('date-time-picker');

            var onDatePickerShown = this.onDateTimePickerShown.bind(this);
            DateTimePickerShownEvent.on(onDatePickerShown);
            this.onRemoved((event: api.dom.ElementRemovedEvent) => {
                DateTimePickerShownEvent.un(onDatePickerShown);
            });

            this.validUserInput = true;

            if (builder.selectedDate) {
                this.setDate(builder.selectedDate);
            }
            if (builder.hours || builder.minutes) {
                this.setTime(builder.hours, builder.minutes);
            }

            this.input = api.ui.text.TextInput.middle(undefined, this.formatDateTime(this.selectedDate));
            this.input.onClicked((e: MouseEvent) => {
                e.preventDefault();
                this.popup.show();
            });

            var wrapper = new api.dom.DivEl('wrapper');
            wrapper.appendChild(this.input);

            var calendar = new CalendarBuilder().
                setSelectedDate(builder.selectedDate).
                setMonth(builder.month).
                setYear(builder.year).
                setInteractive(true).
                build();

            var popupBuilder = new DateTimePickerPopupBuilder().
                setHours(builder.hours).
                setMinutes(builder.minutes).
                setCalendar(calendar).
                setTimezone(builder.timezone).
                setUseLocalTimezoneIfNotPresent(builder.useLocalTimezoneIfNotPresent);
            this.popup = new DateTimePickerPopup(popupBuilder);
            this.popup.onShown(() => {
                new DateTimePickerShownEvent(this).fire();
            });
            wrapper.appendChild(this.popup);

            this.popupTrigger = new api.ui.button.Button();
            this.popupTrigger.addClass('icon-calendar');
            wrapper.appendChild(this.popupTrigger);

            this.appendChild(wrapper);

            this.setupListeners(builder);
        }

        private setupListeners(builder: DateTimePickerBuilder) {

            if (builder.closeOnOutsideClick) {
                let focusoutTimeout = 0;

                this.onFocusOut(() => {
                    focusoutTimeout = setTimeout(() => {
                        this.popup.hide();
                    }, 50);
                });

                this.onFocusIn(() => {
                    clearTimeout(focusoutTimeout);
                });

                // Prevent focus loss on mouse down
                this.popup.onMouseDown((event: MouseEvent) => {
                    event.preventDefault();
                });
            }

            this.popupTrigger.onClicked((e: MouseEvent) => {
                e.preventDefault();

                if (this.popup.isVisible()) {
                    this.popup.hide();
                } else {
                    this.popup.show();
                }
            });

            this.popup.onSelectedDateChanged((e: SelectedDateChangedEvent) => {
                if (builder.closeOnSelect) {
                    this.popup.hide();
                }
                this.setDate(e.getDate());
                this.setInputValue();
            });

            this.popup.onSelectedTimeChanged((hours: number, minutes: number) => {
                this.setTime(hours, minutes);
                this.setInputValue();
            });

            this.input.onKeyUp((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isArrowKey(event) || api.ui.KeyHelper.isModifierKey(event)) {
                    return;
                }
                var typedDateTime = this.input.getValue();
                var date: Date = null;
                if (api.util.StringHelper.isEmpty(typedDateTime)) {
                    this.validUserInput = true;
                    this.setDateTime(null);
                    this.popup.hide();
                } else {
                    date = api.util.DateHelper.parseDateTime(typedDateTime);
                    var dateLength = date && date.getFullYear().toString().length + 12;
                    if (date && date.toString() != "Invalid Date" && typedDateTime.length == dateLength) {
                        this.validUserInput = true;
                        this.setDateTime(date);
                        if (!this.popup.isVisible()) {
                            this.popup.show();
                        }
                    } else {
                        this.selectedDate = null;
                        this.validUserInput = false;
                    }
                }
                this.notifySelectedDateTimeChanged(new SelectedDateChangedEvent(date));
                this.updateInputStyling();
            });

            this.popup.onKeyDown((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isTabKey(event)) {
                    if (!(document.activeElement == this.input.getEl().getHTMLElement())) {
                        event.preventDefault();
                        event.stopPropagation();
                        this.popup.hide();
                        this.popupTrigger.giveFocus();
                    }
                }
            });

            this.input.onKeyDown((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isTabKey(event)) { // handles tab navigation events on date input
                    if (!event.shiftKey) {
                        event.preventDefault();
                        event.stopPropagation();
                        this.popupTrigger.giveFocus();
                    } else {
                        this.popup.hide();
                    }
                }
            });

            this.popupTrigger.onKeyDown((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isTabKey(event)) {
                    this.popup.hide();
                }
            });
        }

        private onDateTimePickerShown(event: DateTimePickerShownEvent) {
            if (event.getDateTimePicker() !== this) {
                this.popup.hide();
            }
        }

        public setSelectedDateTime(date: Date) {
            this.input.setValue(this.formatDateTime(date));
            this.setDateTime(date);
        }

        private setDateTime(date: Date) {
            this.selectedDate = date;
            this.popup.setSelectedDate(date, true);
            date ?
            this.popup.setSelectedTime(date.getHours(), date.getMinutes(), true) :
            this.popup.setSelectedTime(null, null, true);
        }

        private setInputValue() {
            this.validUserInput = true;
            this.input.setValue(this.formatDateTime(this.selectedDate), false, true);
            this.notifySelectedDateTimeChanged(new SelectedDateChangedEvent(this.selectedDate));
            this.updateInputStyling();
        }

        private setTime(hours: number, minutes: number) {
            if (!this.selectedDate) {
                var today = new Date();
                this.selectedDate = new Date(today.getFullYear(), today.getMonth(), today.getDate());
            }
            this.selectedDate.setHours(hours);
            this.selectedDate.setMinutes(minutes);
        }

        private setDate(date: Date) {
            var hours = this.selectedDate ? this.selectedDate.getHours() : 0,
                minutes = this.selectedDate ? this.selectedDate.getMinutes() : 0;

            this.selectedDate = date;

            if (hours || minutes) {
                this.setTime(hours, minutes);
            }
        }

        isDirty(): boolean {
            return this.input.isDirty();
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

        private formatDateTime(date: Date): string {
            if (!date) {
                return "";
            }
            return api.util.DateHelper.formatDate(date) + ' ' + api.util.DateHelper.formatTime(date, false);
        }

        private updateInputStyling() {
            this.input.updateValidationStatusOnUserInput(this.validUserInput);
        }

        giveFocus(): boolean {
            return this.input.giveFocus();
        }
    }

    export class DateTimePickerShownEvent extends api.event.Event {

        private dateTimePicker: DateTimePicker;

        constructor(dateTimePicker: DateTimePicker) {
            super();
            this.dateTimePicker = dateTimePicker;
        }

        getDateTimePicker(): DateTimePicker {
            return this.dateTimePicker;
        }

        static on(handler: (event: DateTimePickerShownEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: DateTimePickerShownEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

    }
}
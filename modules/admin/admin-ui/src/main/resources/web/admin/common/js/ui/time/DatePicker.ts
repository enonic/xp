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

    export class DatePicker extends Picker {

        private calendar: Calendar;

        private selectedDate: Date;

        private selectedDateChangedListeners: {(event: SelectedDateChangedEvent) : void}[] = [];

        constructor(builder: DatePickerBuilder) {
            super(builder, 'date-picker');
        }

        protected initData(builder: DatePickerBuilder) {
            this.initCalendar(builder);
        }

        private initCalendar(builder: DatePickerBuilder) {
            this.calendar = builder.calendar || new CalendarBuilder().
                    setSelectedDate(builder.selectedDate).
                    setMonth(builder.month).
                    setYear(builder.year).
                    setInteractive(true).
                    build();
        }

        protected handleShownEvent() {
            var onDatePickerShown = this.onDatePickerShown.bind(this);
            DatePickerShownEvent.on(onDatePickerShown);
            this.onRemoved((event: api.dom.ElementRemovedEvent) => {
                DatePickerShownEvent.un(onDatePickerShown);
            });
        }

        protected initPopup() {
            this.popup = new DatePickerPopupBuilder().
                setCalendar(this.calendar).
                build();
            this.popup.onShown(() => {
                new DatePickerShownEvent(this).fire();
            });
        }

        protected initInput(builder: DatePickerBuilder) {
            var value;
            if (builder.selectedDate) {
                value = this.formatDate(builder.selectedDate);
                this.popup.setSelectedDate(builder.selectedDate);
                this.selectedDate = builder.selectedDate;
            }

            this.input = api.ui.text.TextInput.middle(undefined, value);
            this.input.onClicked((e: MouseEvent) => {
                e.preventDefault();
                this.togglePopupVisibility();
            });
            this.input.onFocus((e: FocusEvent) =>
                setTimeout(() => {
                    if (!this.popup.isVisible()) {
                        e.preventDefault();
                        this.popup.show();
                    }
                }, 150)
            );
        }

        protected initPopupTrigger() {
            this.popupTrigger = new api.ui.button.Button();
            this.popupTrigger.addClass('icon-calendar');
        }

        protected setupListeners(builder: DatePickerBuilder) {

            if (builder.closeOnOutsideClick) {

                api.util.AppHelper.focusInOut(this, () => {
                    this.popup.hide();
                }, 50, false);

                // Prevent focus loss on mouse down
                this.popup.onMouseDown((event: MouseEvent) => {
                    event.preventDefault();
                });
            }

            this.popupTrigger.onClicked((e: MouseEvent) => {
                e.preventDefault();
                this.togglePopupVisibility();
            });

            this.popup.onSelectedDateChanged((e: SelectedDateChangedEvent) => {
                if (builder.closeOnSelect) {
                    this.popup.hide();
                }
                this.selectedDate = e.getDate();
                this.validUserInput = true;
                this.input.setValue(this.formatDate(e.getDate()), false, true);
                this.notifySelectedDateChanged(e);
                this.updateInputStyling();
            });

            this.input.onKeyUp((event: KeyboardEvent) => {
                var typedDate = this.input.getValue();

                if (api.util.StringHelper.isEmpty(typedDate)) {
                    this.calendar.selectDate(null);
                    this.selectedDate = null;
                    this.validUserInput = true;
                    this.popup.hide();
                    this.notifySelectedDateChanged(new SelectedDateChangedEvent(null));
                } else {
                    var date = api.util.DateHelper.parseDate(typedDate, "-", true);
                    if (date) {
                        this.selectedDate = date;
                        this.validUserInput = true;
                        this.calendar.selectDate(date);
                        this.notifySelectedDateChanged(new SelectedDateChangedEvent(date));
                        if (!this.popup.isVisible()) {
                            this.popup.show();
                        }
                    } else {
                        this.selectedDate = null;
                        this.validUserInput = false;
                        this.notifySelectedDateChanged(new SelectedDateChangedEvent(null));
                    }
                }
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

        private onDatePickerShown(event: DatePickerShownEvent) {
            if (event.getDatePicker() !== this) {
                this.popup.hide();
            }
        }

        setSelectedDate(date: Date) {
            this.input.setValue(this.formatDate(date));
            this.popup.setSelectedDate(date);
            this.selectedDate = date;
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
            return date ? api.util.DateHelper.formatDate(date) : "";
        }
    }

    export class DatePickerShownEvent extends api.event.Event {

        private datePicker: DatePicker;

        constructor(datePicker: DatePicker) {
            super();
            this.datePicker = datePicker;
        }

        getDatePicker(): DatePicker {
            return this.datePicker;
        }

        static on(handler: (event: DatePickerShownEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: DatePickerShownEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

    }
}
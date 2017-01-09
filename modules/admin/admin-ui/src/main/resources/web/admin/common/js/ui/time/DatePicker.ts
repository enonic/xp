module api.ui.time {

    export class DatePickerBuilder {

        year: number;

        month: number;

        selectedDate: Date;

        calendar: Calendar;

        startingDayOfWeek: DayOfWeek = DaysOfWeek.MONDAY;

        closeOnSelect: boolean = true;

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
            let onDatePickerShown = this.onDatePickerShown.bind(this);
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
            let value = "";
            if (builder.selectedDate) {
                value = this.formatDate(builder.selectedDate);
                this.popup.setSelectedDate(builder.selectedDate);
                this.selectedDate = builder.selectedDate;
            }

            this.input = api.ui.text.TextInput.middle(undefined, value);
            this.input.setPlaceholder("YYYY-MM-DD");
        }

        protected setupListeners(builder: DatePickerBuilder) {

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
                if (api.ui.KeyHelper.isArrowKey(event) || api.ui.KeyHelper.isModifierKey(event)) {
                    return;
                }

                let typedDate = this.input.getValue();

                if (api.util.StringHelper.isEmpty(typedDate)) {
                    this.calendar.selectDate(null);
                    this.selectedDate = null;
                    this.validUserInput = true;
                    this.popup.hide();
                    this.notifySelectedDateChanged(new SelectedDateChangedEvent(null));
                } else {
                    let date = api.util.DateHelper.parseDate(typedDate, "-", true);
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
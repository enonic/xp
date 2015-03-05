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

            var onDatePickerShown = this.onDatePickerShown.bind(this);
            DatePickerShownEvent.on(onDatePickerShown);
            this.onRemoved((event: api.dom.ElementRemovedEvent) => {
                DatePickerShownEvent.un(onDatePickerShown);
            });

            this.validUserInput = true;

            this.input = api.ui.text.TextInput.middle();
            this.input.onClicked((e: MouseEvent) => {
                //e.stopPropagation();
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
                setCloseOnOutsideClick(false);
            this.popup = popupBuilder.build();
            this.popup.onShown(() => {
                new DatePickerShownEvent(this).fire();
            });
            wrapper.appendChild(this.popup);

            this.popupTrigger = new api.ui.button.Button();
            this.popupTrigger.addClass('icon-calendar4');
            wrapper.appendChild(this.popupTrigger);

            this.appendChild(wrapper);

            this.popupTrigger.onClicked((e: MouseEvent) => {
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

            this.input.onKeyUp((event: KeyboardEvent) => {
                var typedDate = this.input.getValue();

                if (api.util.StringHelper.isEmpty(typedDate)) {
                    this.calendar.selectDate(null);
                    this.selectedDate = null;
                    this.validUserInput = true;
                    this.popup.hide();
                    this.notifySelectedDateChanged(new SelectedDateChangedEvent(null));
                }
                else {
                    var date = api.util.DateHelper.parseDate(typedDate);
                    if (date) {
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
                this.updateInputStyling();
            });

            this.popup.onKeyDown((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isTabKey(event)) {
                    if(!(document.activeElement == this.input.getEl().getHTMLElement())) {
                        event.preventDefault();
                        event.stopPropagation();
                        this.popup.hide();
                        this.popupTrigger.giveFocus();
                    }
                }
            });

            this.input.onKeyDown((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isTabKey(event)) { // handles tab navigation events on date input
                    if(!event.shiftKey) {
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

            api.dom.Body.get().onKeyDown((e: KeyboardEvent) => this.popupTabListener(e));
            if (builder.closeOnOutsideClick){
                api.dom.Body.get().onClicked((e: MouseEvent) => this.outsideClickListener(e));
            }
        }

        // as popup blur and focus events behave incorrectly - we manually catch tab navigation event in popup below
        private popupTabListener(e: KeyboardEvent) {
            if (api.ui.KeyHelper.isTabKey(e) && !this.getEl().contains(<HTMLElement> e.target)) {
                if (this.popup.isVisible()) {
                    e.stopPropagation();
                    e.preventDefault();
                    this.popupTrigger.getEl().focus();
                    this.popup.hide();
                }
            }
        }

        private outsideClickListener(e: MouseEvent) {
            if (!this.getEl().contains(<HTMLElement> e.target)) {
                this.popup.hide();
            }
        }

        private onDatePickerShown(event: DatePickerShownEvent) {
            if (event.getDatePicker() !== this) {
                this.popup.hide();
            }
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
            return api.util.DateHelper.formatDate(date);
        }

        private updateInputStyling() {
            this.input.updateValidationStatusOnUserInput(this.validUserInput);
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
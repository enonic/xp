module api.ui.time {

    import Timezone = api.util.Timezone;

    export class DateTimePickerPopupBuilder {

        hours: number;

        minutes: number;

        calendar: Calendar;

        timezone: Timezone;

        // use local timezone if timezone value is not initialized
        useLocalTimezoneIfNotPresent: boolean = false;

        closeOnOutsideClick: boolean = true;

        setHours(value: number): DateTimePickerPopupBuilder {
            this.hours = value;
            return this;
        }

        getHours(): number {
            return this.hours;
        }

        setMinutes(value: number): DateTimePickerPopupBuilder {
            this.minutes = value;
            return this;
        }

        getMinutes(): number {
            return this.minutes;
        }

        setCalendar(value: Calendar): DateTimePickerPopupBuilder {
            this.calendar = value;
            return this;
        }

        getCalendar(): Calendar {
            return this.calendar;
        }

        setTimezone(value: Timezone): DateTimePickerPopupBuilder {
            this.timezone = value;
            return this;
        }

        setUseLocalTimezoneIfNotPresent(value: boolean): DateTimePickerPopupBuilder {
            this.useLocalTimezoneIfNotPresent = value;
            return this;
        }

        isUseLocalTimezoneIfNotPresent(): boolean {
            return this.useLocalTimezoneIfNotPresent;
        }

        getTimezone(): Timezone {
            return this.timezone;
        }

        setCloseOnOutsideClick(value: boolean): DateTimePickerPopupBuilder {
            this.closeOnOutsideClick = value;
            return this;
        }

        isCloseOnOutsideClick(): boolean {
            return this.closeOnOutsideClick;
        }

        build(): DateTimePickerPopup {
            return new DateTimePickerPopup(this);
        }

    }

    export class DateTimePickerPopup extends api.dom.DivEl {

        private datePickerPopup: DatePickerPopup;

        private timePickerPopup: TimePickerPopup;

        constructor(builder: DateTimePickerPopupBuilder) {
            super('date-time-dialog');

            var closeOnOutsideClick = builder.isCloseOnOutsideClick();
            builder.setCloseOnOutsideClick(false);

            this.datePickerPopup = new DatePickerPopupBuilder().
                setCalendar(builder.getCalendar()).
                setCloseOnOutsideClick(false).build();
            this.timePickerPopup = new TimePickerPopupBuilder().
                setHours(builder.getHours()).
                setCloseOnOutsideClick(false).
                setTimezone(builder.timezone).
                setUseLocalTimezoneIfNotPresent(builder.useLocalTimezoneIfNotPresent).
                setMinutes(builder.getMinutes()).build();

            this.appendChildren(<api.dom.Element>this.datePickerPopup, <api.dom.Element>this.timePickerPopup);

            if (closeOnOutsideClick) {
                api.dom.Body.get().onClicked((e: MouseEvent) => this.outsideClickListener(e));
            }
        }

        getSelectedDate(): Date {
            return this.datePickerPopup.getSelectedDate();
        }

        onSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.datePickerPopup.onSelectedDateChanged(listener);
        }

        unSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.datePickerPopup.unSelectedDateChanged(listener);
        }

        getSelectedTime(): { hour: number; minute: number } {
            return this.timePickerPopup.getSelectedTime();
        }

        onSelectedTimeChanged(listener: (hours: number, minutes: number) => void) {
            this.timePickerPopup.onSelectedTimeChanged(listener);
        }

        unSelectedTimeChanged(listener: (hours: number, minutes: number) => void) {
            this.timePickerPopup.unSelectedTimeChanged(listener);
        }

        getSelectedDateTime(): Date {
            var date = this.getSelectedDate();
            var time = this.getSelectedTime();
            if (!date || !time) {
                return null;
            }
            return new Date(date.getFullYear(), date.getMonth(), date.getDate(), time.hour, time.minute);
        }

        setSelectedTime(hours: number, minutes: number, silent?: boolean) {
            this.timePickerPopup.setSelectedTime(hours, minutes, silent);
        }

        setSelectedDate(date: Date, silent?: boolean) {
            this.datePickerPopup.setSelectedDate(date, silent);
        }

        private outsideClickListener(e: MouseEvent) {
            if (!this.getEl().contains(<HTMLElement> e.target)) {
                this.hide();
            }
        }
    }
}
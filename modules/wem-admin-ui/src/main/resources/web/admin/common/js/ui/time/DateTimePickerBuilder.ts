module api.ui.time {

    export class DateTimePickerBuilder implements CommonDatePickerBuilder, CommonTimePickerBuilder {

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

        getYear(): number {
            return this.year;
        }

        setMonth(value: number): DateTimePickerBuilder {
            this.month = value;
            return this;
        }

        getMonth(): number {
            return this.month;
        }

        setSelectedDate(value: Date): DateTimePickerBuilder {
            this.selectedDate = value;
            return this;
        }

        getSelectedDate(): Date {
            return this.selectedDate;
        }

        setHours(value: number): DateTimePickerBuilder {
            this.hours = value;
            return this;
        }

        getHours(): number {
            return this.hours;
        }

        setMinutes(value: number): DateTimePickerBuilder {
            this.minutes = value;
            return this;
        }

        getMinutes(): number {
            return this.minutes;
        }

        setStartingDayOfWeek(value: DayOfWeek): DateTimePickerBuilder {
            this.startingDayOfWeek = value;
            return this;
        }

        getStartingDayOfWeek(): DayOfWeek {
            return this.startingDayOfWeek;
        }

        setCloseOnSelect(value: boolean): DateTimePickerBuilder {
            this.closeOnSelect = value;
            return this;
        }

        isCloseOnSelect(): boolean {
            return this.closeOnSelect;
        }

        setCloseOnOutsideClick(value: boolean): DateTimePickerBuilder {
            this.closeOnOutsideClick = value;
            return this;
        }

        isCloseOnOutsideClick(): boolean {
            return this.closeOnOutsideClick;
        }

        build(): DateTimePicker {
            return new DateTimePicker(this);
        }

    }

}
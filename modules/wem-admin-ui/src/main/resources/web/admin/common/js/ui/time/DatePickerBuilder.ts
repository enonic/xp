module api.ui.time {

    export interface CommonDatePickerBuilder {

        getYear(): number;

        getMonth(): number;

        getSelectedDate(): Date;

        getStartingDayOfWeek(): DayOfWeek;

        isCloseOnSelect(): boolean;

        isCloseOnOutsideClick(): boolean;

    }

    export class DatePickerBuilder implements CommonDatePickerBuilder {

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

        getYear(): number {
            return this.year;
        }

        setMonth(value: number): DatePickerBuilder {
            this.month = value;
            return this;
        }

        getMonth(): number {
            return this.month;
        }

        setSelectedDate(value: Date): DatePickerBuilder {
            this.selectedDate = value;
            return this;
        }

        getSelectedDate(): Date {
            return this.selectedDate;
        }

        setStartingDayOfWeek(value: DayOfWeek): DatePickerBuilder {
            this.startingDayOfWeek = value;
            return this;
        }

        getStartingDayOfWeek(): DayOfWeek {
            return this.startingDayOfWeek;
        }

        setCloseOnSelect(value: boolean): DatePickerBuilder {
            this.closeOnSelect = value;
            return this;
        }

        isCloseOnSelect(): boolean {
            return this.closeOnSelect;
        }

        setCloseOnOutsideClick(value: boolean): DatePickerBuilder {
            this.closeOnOutsideClick = value;
            return this;
        }

        isCloseOnOutsideClick(): boolean {
            return this.closeOnOutsideClick;
        }

        build(): DatePicker {
            return new DatePicker(this);
        }

    }

}
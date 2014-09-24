module api.ui.time {

    export interface CommonTimePickerBuilder {

        getHours(): number;

        getMinutes(): number;

        isCloseOnOutsideClick(): boolean;

    }

    export class TimePickerBuilder implements CommonTimePickerBuilder {

        hours: number;

        minutes: number;

        closeOnOutsideClick: boolean = true;

        setHours(value: number): TimePickerBuilder {
            this.hours = value;
            return this;
        }

        getHours(): number {
            return this.hours;
        }

        setMinutes(value: number): TimePickerBuilder {
            this.minutes = value;
            return this;
        }

        getMinutes(): number {
            return this.minutes;
        }

        setCloseOnOutsideClick(value: boolean): TimePickerBuilder {
            this.closeOnOutsideClick = value;
            return this;
        }

        isCloseOnOutsideClick(): boolean {
            return this.closeOnOutsideClick;
        }

        build(): TimePicker {
            return new TimePicker(this);
        }

    }

}
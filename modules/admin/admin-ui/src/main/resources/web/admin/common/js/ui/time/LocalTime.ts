module api.ui.time {

    export class LocalTime extends api.dom.DivEl {

        private timePicker: api.ui.time.TimePicker;


        constructor(timeBuilder?: api.ui.time.TimePickerBuilder) {
            super("date-picker");


            if (!timeBuilder) {
                timeBuilder = new api.ui.time.TimePickerBuilder()
            }
            this.timePicker = timeBuilder.build();


            this.layoutItems();
        }

        private layoutItems() {
            this.removeChildren();

            this.appendChild(this.timePicker);
            return this;
        }

        isDirty(): boolean {
            return this.timePicker.isDirty();
        }

        onTimeChanged(listener: (hours: number, minutes: number)=>void) {
            this.timePicker.onSelectedTimeChanged(listener);
        }

        setTime(value: api.util.LocalTime): LocalTime {
            var localTime = value.getAdjustedTime();
            this.timePicker = new api.ui.time.TimePickerBuilder().setHours(localTime.hour).setMinutes(localTime.minute).build();
            this.layoutItems();
            return this;
        }

        giveFocus(): boolean {
            return this.timePicker.giveFocus();
        }

        getTimePicker(): api.ui.time.TimePicker {
            return this.timePicker;
        }
    }
}
module api.ui.time {

    export class DateTime extends api.dom.DivEl {

        private timePicker: api.ui.time.TimePicker;

        private datePicker: api.ui.time.DatePicker;

        constructor(dateBuilder?: api.ui.time.DatePickerBuilder, timeBuilder?: api.ui.time.TimePickerBuilder) {
            super("date-picker");

            if (!dateBuilder) {
                dateBuilder = new api.ui.time.DatePickerBuilder()
            }
            if (!timeBuilder) {
                timeBuilder = new api.ui.time.TimePickerBuilder()
            }
            this.timePicker = timeBuilder.build();
            this.datePicker = dateBuilder.build();

            this.layoutItems();

            this.onShown((event) => {
                this.timePicker.giveFocus();
            })
        }

        private layoutItems() {
            this.removeChildren();
            this.appendChild(this.datePicker);
            this.appendChild(this.timePicker);
            return this;
        }

        onDateChanged(listener: (event: api.ui.time.SelectedDateChangedEvent)=>void) {
            this.datePicker.onSelectedDateChanged(listener);
        }

        onTimeChanged(listener: (hours: number, minutes: number)=>void) {
            this.timePicker.onSelectedTimeChanged(listener);
        }

        getSelectedTime(): string {
            return this.timePicker.formatTime(this.timePicker.getSelectedTime().hour, this.timePicker.getSelectedTime().minute);

        }

        getSelectedDate(): string {
            if (!this.datePicker.getSelectedDate()) {
                return null;
            }
            return this.datePicker.getSelectedDate().toDateString();

        }

    }
}
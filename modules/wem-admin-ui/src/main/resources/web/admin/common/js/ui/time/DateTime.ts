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

        getSelectedDateTime(): Date {
            var date = this.datePicker.getSelectedDate();
            var time = this.timePicker.getSelectedTime();
            if (!date || !time) {
                return null;
            }
            return api.util.DateHelper.newUTCDateTime(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(),
                time.hour, time.minute);
        }
    }
}
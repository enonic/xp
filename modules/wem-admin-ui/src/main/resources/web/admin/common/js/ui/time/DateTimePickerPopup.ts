module api.ui.time {

    export class DateTimePickerPopup extends api.dom.DivEl {

        private datePickerPopup: DatePickerPopup;

        private timePickerPopup: TimePickerPopup;

        constructor(calendar: Calendar, builder: DateTimePickerBuilder) {
            super('date-time-dialog');

            var closeOnOutsideClick = builder.isCloseOnOutsideClick();
            builder.setCloseOnOutsideClick(false);

            this.datePickerPopup = new DatePickerPopup(calendar, builder);
            this.timePickerPopup = new TimePickerPopup(builder);

            this.appendChildren([this.datePickerPopup, this.timePickerPopup]);

            if (closeOnOutsideClick) {
                api.dom.Body.get().onClicked((e: MouseEvent) => this.outsideClickListener(e));
            }
        }

        getSelectedDate(): Date {
            return this.datePickerPopup.getCalendar().getSelectedDate();
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
            return api.util.DateHelper.newUTCDateTime(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(),
                time.hour, time.minute);
        }

        setSelectedTime(hours: number, minutes: number, silent?: boolean) {
            this.timePickerPopup.setSelectedTime(hours, minutes, silent);
        }

        private outsideClickListener(e: MouseEvent) {
            if (!this.getEl().contains(<HTMLElement> e.target)) {
                this.hide();
            }
        }
    }
}
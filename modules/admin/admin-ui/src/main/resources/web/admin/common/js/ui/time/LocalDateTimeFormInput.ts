module api.form {

    import DateTimePickerBuilder = api.ui.time.DateTimePickerBuilder;
    import DateTimePicker = api.ui.time.DateTimePicker;

    export class LocalDateTimeFormInput extends api.dom.FormInputEl {

        private localDate: DateTimePicker;

        constructor(value?: Date) {
            super("div");

            var publishFromDateTimeBuilder = new DateTimePickerBuilder();
            if (value) {
                publishFromDateTimeBuilder.setSelectedDate(value);
                publishFromDateTimeBuilder.setHours(value.getHours());
                publishFromDateTimeBuilder.setMinutes(value.getMinutes());
            }

            this.localDate = publishFromDateTimeBuilder.build();
            this.appendChild(this.localDate);
        }

        doGetValue(): string {
            return this.localDate.toString();
        }

        protected doSetValue(value: string, silent?: boolean) {
            this.localDate.setSelectedDateTime(api.util.LocalDateTime.fromString(value).toDate());
        }

        getPicker(): DateTimePicker {
            return this.localDate;
        }
    }
}
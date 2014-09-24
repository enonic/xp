module api.content.form.inputtype.time {

    import support = api.form.inputtype.support;
    import ValueTypes = api.data.type.ValueTypes;

    export class DateTime extends support.BaseInputTypeNotManagingAdd<any,Date> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        getValueType(): api.data.type.ValueType {
            return api.data.type.ValueTypes.LOCAL_DATE_TIME;
        }

        newInitialValue(): Date {
            return null;
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var dateTimeBuilder = new api.ui.time.DateTimePickerBuilder();

            if (property.hasNonNullValue()) {
                var date = property.getDate();
                dateTimeBuilder.
                    setYear(date.getUTCFullYear()).
                    setMonth(date.getUTCMonth()).
                    setSelectedDate(date).
                    setHours(date.getUTCHours()).
                    setMinutes(date.getUTCMinutes());
            }

            return new api.ui.time.DateTimePicker(dateTimeBuilder);
        }

        availableSizeChanged() {
            // Nothing
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {

            var dateTime = <api.ui.time.DateTimePicker>element;
            dateTime.onSelectedTimeChanged((hours: number, minutes: number) => {
                var selectedDateTime = dateTime.getSelectedDateTime();
                var newValue = new api.data.Value(selectedDateTime, ValueTypes.LOCAL_DATE_TIME);
                listener(new api.form.inputtype.support.ValueChangedEvent(newValue));
            });
            dateTime.onSelectedDateChanged((event: api.ui.time.SelectedDateChangedEvent) => {
                var selectedDateTime = dateTime.getSelectedDateTime();
                var newValue = new api.data.Value(selectedDateTime, ValueTypes.LOCAL_DATE_TIME);
                listener(new api.form.inputtype.support.ValueChangedEvent(newValue));
            });
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var dateTime: api.ui.time.DateTimePicker = < api.ui.time.DateTimePicker>occurrence;
            var selectedDateTime = dateTime.getSelectedDateTime();
            return new api.data.Value(selectedDateTime, ValueTypes.LOCAL_DATE_TIME);
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            if (value == null) {
                return true;
            }
            return !value.getType().equals(ValueTypes.LOCAL_DATE_TIME);
        }
    }
    api.form.inputtype.InputTypeManager.register(new api.Class("DateTime", DateTime));

}
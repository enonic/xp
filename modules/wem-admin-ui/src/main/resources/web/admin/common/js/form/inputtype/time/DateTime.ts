module api.content.form.inputtype.time {

    import support = api.form.inputtype.support;
    import ValueTypes = api.data.type.ValueTypes;

    export class DateTime extends support.BaseInputTypeNotManagingAdd<any> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        getValueType(): api.data.type.ValueType {
            return api.data.type.ValueTypes.LOCAL_DATE_TIME;
        }

        newInitialValue(): api.data.Value {
            return null;
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var dateBuilder = new api.ui.time.DatePickerBuilder();
            var timeBuilder = new api.ui.time.TimePickerBuilder();

            if (property.hasNonNullValue()) {
                var date = property.getDate();
                dateBuilder.
                    setYear(date.getUTCFullYear()).
                    setMonth(date.getUTCMonth()).
                    setSelectedDate(date);
                timeBuilder.
                    setHours(date.getUTCHours()).
                    setMinutes(date.getUTCMinutes());
            }

            return new api.ui.time.DateTime(dateBuilder, timeBuilder);
        }

        availableSizeChanged() {
            // Nothing
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {

            var dateTime = <api.ui.time.DateTime>element;
            dateTime.onTimeChanged((hours: number, minutes: number) => {
                var selectedDateTime = dateTime.getSelectedDateTime();
                var newValue = new api.data.Value(selectedDateTime, ValueTypes.LOCAL_DATE_TIME);
                listener(new api.form.inputtype.support.ValueChangedEvent(newValue));
            });
            dateTime.onDateChanged((event: api.ui.time.SelectedDateChangedEvent) => {
                var selectedDateTime = dateTime.getSelectedDateTime();
                var newValue = new api.data.Value(selectedDateTime, ValueTypes.LOCAL_DATE_TIME);
                listener(new api.form.inputtype.support.ValueChangedEvent(newValue));
            });
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var dateTime: api.ui.time.DateTime = < api.ui.time.DateTime>occurrence;
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
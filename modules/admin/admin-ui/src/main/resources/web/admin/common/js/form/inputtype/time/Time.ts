module api.content.form.inputtype.time {

    import support = api.form.inputtype.support;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    /**
     * Uses [[api.data.ValueType]] [[api.data.ValueTypeLocalTime]].
     */
    export class Time extends support.BaseInputTypeNotManagingAdd<api.util.LocalTime> {

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
        }

        getValueType(): ValueType {
            return ValueTypes.LOCAL_TIME;
        }

        newInitialValue(): Value {
            return ValueTypes.LOCAL_TIME.newNullValue();
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            if (!ValueTypes.LOCAL_TIME.equals(property.getType())) {
                property.convertValueType(ValueTypes.LOCAL_TIME);
            }
            
            var value = this.getValueFromProperty(property);
            var timePicker = new api.ui.time.TimePickerBuilder().setHours(value.hours).setMinutes(value.minutes).build();

            timePicker.onSelectedTimeChanged((hours: number, minutes: number) => {
                var valueStr = hours + ':' + minutes;
                var value = new Value(api.util.LocalTime.isValidString(valueStr) ? api.util.LocalTime.fromString(valueStr) : null,
                    ValueTypes.LOCAL_TIME);
                this.notifyOccurrenceValueChanged(timePicker, value);
            });

            return timePicker;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly: boolean) {
            var localTime = <api.ui.time.TimePicker> occurrence;

            if (!unchangedOnly || !localTime.isDirty()) {
                var value = this.getValueFromProperty(property);
                localTime.setSelectedTime(value.hours, value.minutes);
            }
        }

        private getValueFromProperty(property: api.data.Property): {hours: number; minutes: number} {
            var hours = -1,
                minutes = -1;
            if (property && property.hasNonNullValue()) {
                var localTime: api.util.LocalTime = property.getLocalTime();
                if (localTime) {
                    var adjustedTime = localTime.getAdjustedTime();
                    hours = adjustedTime.hour;
                    minutes = adjustedTime.minute;
                }
            }
            return {
                hours: hours,
                minutes: minutes
            }
        }

        availableSizeChanged() {
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.LOCAL_TIME);
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {
            var timePicker = <api.ui.time.TimePicker> inputElement;
            return timePicker.hasValidUserInput();
        }

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("Time", Time));

}
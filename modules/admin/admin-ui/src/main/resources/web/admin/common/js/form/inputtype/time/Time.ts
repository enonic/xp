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
            return super.newInitialValue() || ValueTypes.LOCAL_TIME.newNullValue();
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            if (!ValueTypes.LOCAL_TIME.equals(property.getType())) {
                property.convertValueType(ValueTypes.LOCAL_TIME);
            }

            const value = this.getValueFromProperty(property);
            const timePicker = new api.ui.time.TimePickerBuilder().setHours(value.hours).setMinutes(value.minutes).build();

            timePicker.onSelectedTimeChanged((hours: number, minutes: number) => {
                const valueStr = hours + ':' + minutes;
                const newValue = new Value(api.util.LocalTime.isValidString(valueStr) ? api.util.LocalTime.fromString(valueStr) : null,
                    ValueTypes.LOCAL_TIME);
                this.notifyOccurrenceValueChanged(timePicker, newValue);
            });

            return timePicker;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly: boolean) {
            let localTime = <api.ui.time.TimePicker> occurrence;

            if (!unchangedOnly || !localTime.isDirty() || !localTime.isValid()) {

                let value = this.getValueFromProperty(property);
                localTime.setSelectedTime(value.hours, value.minutes);
            }
        }

        resetInputOccurrenceElement(occurrence: api.dom.Element) {
            let input = <api.ui.time.TimePicker> occurrence;

            input.resetBase();
        }

        private getValueFromProperty(property: api.data.Property): {hours: number; minutes: number} {
            let hours = -1,
                minutes = -1;
            if (property && property.hasNonNullValue()) {
                let localTime: api.util.LocalTime = property.getLocalTime();
                if (localTime) {
                    let adjustedTime = localTime.getAdjustedTime();
                    hours = adjustedTime.hour;
                    minutes = adjustedTime.minute;
                }
            }
            return {
                hours: hours,
                minutes: minutes
            };
        }

        availableSizeChanged() {
            // must be implemented by children
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.LOCAL_TIME);
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {
            let timePicker = <api.ui.time.TimePicker> inputElement;
            return timePicker.isValid();
        }

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("Time", Time));

}
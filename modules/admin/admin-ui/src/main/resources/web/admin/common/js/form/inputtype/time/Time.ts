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
            var localTimeEl = new api.ui.time.LocalTime();

            var timeValue: api.util.LocalTime = property.getLocalTime();
            if (timeValue) {
                localTimeEl.setTime(timeValue);
            }

            localTimeEl.onTimeChanged((hours: number, minutes: number) => {
                var changedValue: Value = ValueTypes.LOCAL_TIME.newValue(hours + ":" + minutes);
                property.setValue(changedValue);
                this.validate(false);
            });

            property.onPropertyValueChanged((event: api.data.PropertyValueChangedEvent) => {
                this.updateInputOccurrenceElement(localTimeEl, property, true);
            });

            return localTimeEl;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly: boolean) {
            var localTime = <api.ui.time.LocalTime> occurrence;

            if (!unchangedOnly || !localTime.isDirty()) {
                localTime.setTime(property.getLocalTime());
            }
        }

        availableSizeChanged() {
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.LOCAL_TIME);
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {
            var localTime = <api.ui.time.LocalTime>inputElement;
            var timePicker = localTime.getTimePicker();
            return timePicker.hasValidUserInput();
        }

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("Time", Time));

}
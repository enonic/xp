module api.content.form.inputtype.number.double {

    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import Value = api.data.Value;
    import Property = api.data.Property;

    export class Double extends NumberInputType {

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
        }

        getValueType(): ValueType {
            return ValueTypes.DOUBLE;
        }

        newInitialValue(): Value {
            return super.newInitialValue() || ValueTypes.DOUBLE.newNullValue();
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            if (!ValueTypes.DOUBLE.equals(property.getType())) {
                property.convertValueType(ValueTypes.DOUBLE);
            }

            let inputEl = api.ui.text.TextInput.middle(undefined, this.getPropertyValue(property));
            inputEl.setName(this.getInput().getName() + '-' + property.getIndex());

            inputEl.onValueChanged((event: api.ValueChangedEvent) => {
                let isValid = this.isValid(event.getNewValue());
                let value = isValid ? ValueTypes.DOUBLE.newValue(event.getNewValue()) : this.newInitialValue();

                this.notifyOccurrenceValueChanged(inputEl, value);
                inputEl.updateValidationStatusOnUserInput(isValid);
            });

            return inputEl;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly ?: boolean) {
            let input = <api.ui.text.TextInput> occurrence;

            if (!unchangedOnly || !input.isDirty()) {
                input.setValue(this.getPropertyValue(property));
            }
        }

        resetInputOccurrenceElement(occurrence: api.dom.Element) {
            let input = <api.ui.text.TextInput> occurrence;

            input.resetBaseValues();
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.DOUBLE);
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element, recording ?: api.form.inputtype.InputValidationRecording) {
            let value = <api.ui.text.TextInput>inputElement;

            return this.isValid(value.getValue(), recording);
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class('Double', Double));
}

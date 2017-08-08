module api.form.inputtype.text {

    import support = api.form.inputtype.support;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import FormInputEl = api.dom.FormInputEl;

    export class TextArea extends TextInputType {

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
            this.readConfig(config.inputConfig);
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return super.newInitialValue() || new Value('', ValueTypes.STRING);
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            if (!ValueTypes.STRING.equals(property.getType())) {
                property.convertValueType(ValueTypes.STRING);
            }

            const value = property.hasNonNullValue() ? property.getString() : undefined;
            const inputEl = new api.ui.text.TextArea(this.getInput().getName() + '-' + index, value);

            inputEl.onValueChanged((event: api.ValueChangedEvent) => {
                const newValue = ValueTypes.STRING.newValue(event.getNewValue());
                this.notifyOccurrenceValueChanged(inputEl, newValue);
            });

            this.initOccurenceListeners(inputEl);

            return inputEl;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly: boolean) {
            let input = <api.ui.text.TextArea> occurrence;

            if (!unchangedOnly || !input.isDirty()) {
                input.setValue(property.getString());
            }
        }

        resetInputOccurrenceElement(occurrence: api.dom.Element) {
            let input = <api.ui.text.TextArea> occurrence;

            input.resetBaseValues();
        }

        private newValue(s: string): Value {
            return new Value(s, ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.STRING) ||
                   api.util.StringHelper.isBlank(value.getString());
        }

        hasInputElementValidUserInput(inputElement: FormInputEl, recording?: api.form.inputtype.InputValidationRecording) {
            let textInput = inputElement;
            return this.isValid(textInput.getValue(), textInput, true, recording);
        }

        static getName(): api.form.InputTypeName {
            return new api.form.InputTypeName('TextArea', false);
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class(TextArea.getName().getName(), TextArea));
}

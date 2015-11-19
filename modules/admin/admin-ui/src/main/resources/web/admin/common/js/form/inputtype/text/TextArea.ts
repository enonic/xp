module api.form.inputtype.text {

    import support = api.form.inputtype.support;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class TextArea extends support.BaseInputTypeNotManagingAdd<string> {

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return new Value("", ValueTypes.STRING);
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {

            var inputEl = new api.ui.text.TextArea(this.getInput().getName() + "-" + index);
            if (property.hasNonNullValue()) {
                inputEl.setValue(property.getString());
            }

            inputEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                property.setValue(this.newValue(event.getNewValue()));
            });

            property.onPropertyValueChanged((event: api.data.PropertyValueChangedEvent) => {
                this.updateInputOccurrenceElement(inputEl, property, true);
            })

            return inputEl;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly: boolean) {
            var input = <api.ui.text.TextArea> occurrence;

            if (!unchangedOnly || !input.isDirty()) {
                input.setValue(property.getString());
            }
        }

        private newValue(s: string): Value {
            return new Value(s, ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.STRING) ||
                   api.util.StringHelper.isBlank(value.getString());
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {

            // TODO
            return true;
        }

        static getName(): api.form.InputTypeName {
            return new api.form.InputTypeName("TextArea", false);
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class(TextArea.getName().getName(), TextArea));
}
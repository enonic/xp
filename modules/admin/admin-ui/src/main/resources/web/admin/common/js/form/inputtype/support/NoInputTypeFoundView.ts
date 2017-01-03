module api.form.inputtype.support {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class NoInputTypeFoundView extends BaseInputTypeNotManagingAdd<string> {

        constructor(context: api.form.inputtype.InputTypeViewContext) {
            super(context);
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return super.newInitialValue() || ValueTypes.STRING.newValue("");
        }

        layout(input: api.form.Input, property?: PropertyArray): wemQ.Promise<void> {

            let divEl = new api.dom.DivEl();
            divEl.getEl().setInnerHtml("Warning: no input type found: " + input.getInputType().toString());

            return super.layout(input, property);
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {

            let inputEl = api.ui.text.TextInput.middle();
            inputEl.setName(this.getInput().getName());
            if (property != null) {
                inputEl.setValue(property.getString());
            }
            inputEl.onValueChanged((event: api.ValueChangedEvent) => {
                let value = ValueTypes.STRING.newValue(event.getNewValue());
                this.notifyOccurrenceValueChanged(inputEl, value);
            });

            return inputEl;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly: boolean) {
            let input = <api.ui.text.TextInput> occurrence;

            if (!unchangedOnly || !input.isDirty()) {
                input.setValue(property.getString());
            }
        }

        resetInputOccurrenceElement(occurrence: api.dom.Element) {
            let input = <api.ui.text.TextInput> occurrence;

            input.resetBaseValues();
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.STRING) ||
                   api.util.StringHelper.isBlank(value.getString());
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {

            // TODO
            return true;
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("NoInputTypeFound", NoInputTypeFoundView));
}
module api.form.inputtype.support {

    import Property = api.data2.Property;
    import PropertyArray = api.data2.PropertyArray;
    import Value = api.data2.Value;
    import ValueType = api.data2.ValueType;
    import ValueTypes = api.data2.ValueTypes;

    export class NoInputTypeFoundView extends BaseInputTypeNotManagingAdd<any,string> {

        constructor(context: api.form.inputtype.InputTypeViewContext<any>) {
            super(context);
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return ValueTypes.STRING.newValue("");
        }

        layout(input: api.form.Input, property?: PropertyArray) {

            var divEl = new api.dom.DivEl();
            divEl.getEl().setInnerHtml("Warning: no input type found: " + input.getInputType().toString());

            super.layout(input, property);
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {

            var inputEl = api.ui.text.TextInput.middle();
            inputEl.setName(this.getInput().getName());
            if (property != null) {
                inputEl.setValue(property.getString());
            }
            inputEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                property.setValue(ValueTypes.STRING.newValue(event.getNewValue()));
            });
            return inputEl;
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.STRING) ||
                   api.util.StringHelper.isBlank(value.getString());
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("NoInputTypeFound", NoInputTypeFoundView));
}
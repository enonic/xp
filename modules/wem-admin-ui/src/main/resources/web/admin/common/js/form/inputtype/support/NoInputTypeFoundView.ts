module api.form.inputtype.support {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

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

        layout(input: api.form.Input, property?: PropertyArray): wemQ.Promise<void> {

            var divEl = new api.dom.DivEl();
            divEl.getEl().setInnerHtml("Warning: no input type found: " + input.getInputType().toString());

            return super.layout(input, property);
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
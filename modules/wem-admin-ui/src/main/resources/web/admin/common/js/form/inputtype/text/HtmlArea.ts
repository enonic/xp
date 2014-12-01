module api.form.inputtype.text {

    import support = api.form.inputtype.support;
    import Property = api.data2.Property;
    import Value = api.data2.Value;
    import ValueType = api.data2.ValueType;
    import ValueTypes = api.data2.ValueTypes;

    export class HtmlArea extends support.BaseInputTypeNotManagingAdd<any,string> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return ValueTypes.STRING.newValue("");
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {

            var textAreaEl = new api.ui.text.TextArea(this.getInput().getName() + "-" + index);
            if (property.hasNonNullValue()) {
                textAreaEl.setValue(property.getString());
            }
            textAreaEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                var value = this.newValue(event.getNewValue());
                property.setValue(value);
            });
            return textAreaEl;
        }

        private newValue(s: string): Value {
            return new Value(s, ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.HTML_PART) ||
                   api.util.StringHelper.isBlank(value.getString());
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("HtmlArea", HtmlArea));
}
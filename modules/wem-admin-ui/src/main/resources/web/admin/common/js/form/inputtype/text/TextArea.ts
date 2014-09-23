module api.form.inputtype.text {

    import support = api.form.inputtype.support;

    export class TextArea extends support.BaseInputTypeNotManagingAdd<{},string> {

        constructor(config: api.form.inputtype.InputTypeViewContext<{}>) {
            super(config);
        }

        getValueType(): api.data.type.ValueType {
            return api.data.type.ValueTypes.STRING;
        }

        newInitialValue(): string {
            return "";
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var inputEl = new api.ui.text.TextArea(this.getInput().getName() + "-" + index);
            if (property.hasNonNullValue()) {
                inputEl.setValue(property.getString());
            }
            return inputEl;
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            var inputEl = <api.ui.text.TextArea>element;
            inputEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(event.getNewValue())));
            });
        }

        private newValue(s: string): api.data.Value {
            return new api.data.Value(s, api.data.type.ValueTypes.STRING);
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var inputEl = <api.ui.text.TextArea>occurrence;
            return this.newValue(inputEl.getValue());
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {

            if (value == null) {
                return true;
            }

            if (api.util.StringHelper.isBlank(value.asString())) {
                return true;
            } else {
                return false;
            }
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("TextArea", TextArea));
}
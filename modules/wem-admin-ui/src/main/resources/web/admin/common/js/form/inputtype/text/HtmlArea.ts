module api.form.inputtype.text {

    import support = api.form.inputtype.support;

    export class HtmlArea extends support.BaseInputTypeView<any> {

        constructor(config: api.form.inputtype.InputTypeViewConfig<any>) {
            super(config);
        }

        newInitialValue(): api.data.Value {
            return new api.data.Value("", api.data.ValueTypes.STRING);
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var textAreaEl = new api.ui.text.TextArea(this.getInput().getName() + "-" + index);
            if (property != null) {
                textAreaEl.setValue(property.getString());
            }

            return textAreaEl;
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            var inputEl = <api.ui.text.TextArea>element;
            inputEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(event.getNewValue())));
            });
        }

        private newValue(s: string): api.data.Value {
            return new api.data.Value(s, api.data.ValueTypes.STRING);
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var inputEl = <api.ui.text.TextArea>occurrence;
            return this.newValue(inputEl.getValue());
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            if (value == null) {
                return true;
            }
            return this.stringValueBreaksRequiredContract(value.asString());
        }

        private stringValueBreaksRequiredContract(value: string): boolean {
            return api.util.isStringBlank(value);
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("HtmlArea", HtmlArea));
}
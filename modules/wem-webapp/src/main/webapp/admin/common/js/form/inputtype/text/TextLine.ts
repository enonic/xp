module api.form.inputtype.text {

    import support = api.form.inputtype.support;

    export class TextLine extends support.BaseInputTypeView<any> {

        constructor(config: api.form.inputtype.InputTypeViewConfig<any>) {
            super(config);
        }

        newInitialValue(): api.data.Value {
            return new api.data.Value("", api.data.ValueTypes.STRING);
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var inputEl = api.ui.TextInput.middle();

            if (property != null) {
                inputEl.setName(this.getInput().getName() + "-" + property.getArrayIndex());
                inputEl.setValue(property.getString());
            }
            else {
                inputEl.setName(this.getInput().getName());
            }
            return inputEl;
        }

        availableSizeChanged() {
        }

        addOnValueChangedListener(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            var inputEl = <api.ui.TextInput>element;
            inputEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(event.getNewValue())));
            });
        }

        private newValue(s: string): api.data.Value {
            return new api.data.Value(s, api.data.ValueTypes.STRING);
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var inputEl: api.ui.TextInput = <api.ui.TextInput>occurrence;
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

    api.form.inputtype.InputTypeManager.register("TextLine", TextLine);
}
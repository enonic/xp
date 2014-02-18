module api.form.input.support {

    export class NoInputTypeFoundView extends api.form.inputtype.support.BaseInputTypeView<any> {

        constructor(config: api.form.inputtype.InputTypeViewConfig<any>) {
            super(config);
        }

        layout(input:api.form.Input, properties?:api.data.Property[]) {

            var divEl = new api.dom.DivEl();
            divEl.getEl().setInnerHtml("Warning: no input type found: " + input.getInputType().toString());

            super.layout(input, properties);
        }

        createInputOccurrenceElement(index:number, property:api.data.Property):api.dom.Element {

            var inputEl = api.ui.TextInput.middle();
            inputEl.setName(this.getInput().getName());
            if (property != null) {
                inputEl.setValue(property.getString());
            }
            return inputEl;
        }

        addOnValueChangedListener(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            var inputEl = <api.ui.TextInput>element;
            inputEl.addListener({
                onValueChanged: (oldValue: string, newValue: string) => {
                    listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(oldValue), this.newValue(newValue)));
                }
            });
        }

        private newValue(s: string): api.data.Value {
            return new api.data.Value(s, api.data.ValueTypes.STRING);
        }

        getValue(occurrence:api.dom.Element):api.data.Value {
            var inputEl = <api.ui.TextInput>occurrence;
            return this.newValue(inputEl.getValue());
        }

        valueBreaksRequiredContract(value:api.data.Value):boolean {
            if (value == null) {
                return true;
            }
            return this.stringValueBreaksRequiredContract(value.asString());
        }

        private stringValueBreaksRequiredContract(value: string): boolean {
            return api.util.isStringBlank(value);
        }
    }

    api.form.input.InputTypeManager.register("NoInputTypeFound", NoInputTypeFoundView);
}
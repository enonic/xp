module api.content.form.inputtype.long {

    import support = api.form.inputtype.support;

    export class Long extends support.BaseInputTypeNotManagingAdd<any> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        newInitialValue(): api.data.Value {
            return new api.data.Value("", api.data.ValueTypes.STRING);
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {
            var inputEl = api.ui.text.TextInput.middle();

            if (property != null) {
                inputEl.setName(this.getInput().getName() + "-" + property.getArrayIndex());
                inputEl.setValue(property.getValue().asString());
            }
            else {
                inputEl.setName(this.getInput().getName());
            }
            return inputEl;
        }

        availableSizeChanged() {
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            var inputEl = <api.ui.text.TextInput>element;
            inputEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                listener(new api.form.inputtype.support.ValueChangedEvent(this.newValue(Number(event.getNewValue()).toString())));
            });
        }

        private newValue(s: string): api.data.Value {
            return new api.data.Value(s, api.data.ValueTypes.STRING);
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var inputEl: api.ui.text.TextInput = <api.ui.text.TextInput>occurrence;
            return this.newValue(Number(inputEl.getValue()).toString());
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            if (value == null) {
                return true;
            }
            if (api.util.isStringBlank(value.asString())) {
                return true;
            } else {
                if (isNaN(parseInt(value.asString()))) {
                    throw new Error('Value is not a Number');
                }
                return false;
            }
        }

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("Long", Long));

}
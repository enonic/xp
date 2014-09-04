module api.content.form.inputtype.long {

    import BaseInputTypeNotManagingAdd = api.form.inputtype.support.BaseInputTypeNotManagingAdd;
    import ValueTypes = api.data.type.ValueTypes;

    export class Long extends BaseInputTypeNotManagingAdd<any> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        newInitialValue(): api.data.Value {
            return null;
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

                var value = ValueTypes.LONG.newValue(event.getNewValue());
                if (value) {
                    listener(new api.form.inputtype.support.ValueChangedEvent(value));
                }
            });
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var inputEl: api.ui.text.TextInput = <api.ui.text.TextInput>occurrence;
            return ValueTypes.LONG.newValue(inputEl.getValue());
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            if (value == null) {
                return true;
            }
            return !value.getType().equals(ValueTypes.LONG);
        }

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("Long", Long));

}
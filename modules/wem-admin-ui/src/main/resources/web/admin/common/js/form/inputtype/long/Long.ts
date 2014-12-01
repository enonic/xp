module api.content.form.inputtype.long {

    import BaseInputTypeNotManagingAdd = api.form.inputtype.support.BaseInputTypeNotManagingAdd;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class Long extends BaseInputTypeNotManagingAdd<any,number> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        getValueType(): ValueType {
            return ValueTypes.LONG;
        }

        newInitialValue(): Value {
            return ValueTypes.LONG.newNullValue();
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            var inputEl = api.ui.text.TextInput.middle();
            inputEl.setName(this.getInput().getName() + "-" + property.getIndex());
            inputEl.setValue(!property.hasNullValue() ? property.getString() : "");

            inputEl.onValueChanged((event: api.ui.ValueChangedEvent) => {

                var value = ValueTypes.LONG.newValue(event.getNewValue());
                property.setValue(value);
            });

            return inputEl;
        }

        availableSizeChanged() {
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.LONG);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("Long", Long));
}
module api.content.form.inputtype.long {

    import BaseInputTypeNotManagingAdd = api.form.inputtype.support.BaseInputTypeNotManagingAdd;
    import ValueTypes = api.data.type.ValueTypes;

    export class Long extends BaseInputTypeNotManagingAdd<any,number> {

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        getValueType(): api.data.type.ValueType {
            return ValueTypes.LONG;
        }

        newInitialValue(): number {
            return null;
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {
            var inputEl = api.ui.text.TextInput.middle();
            inputEl.setName(this.getInput().getName() + "-" + property.getArrayIndex());
            inputEl.setValue(!property.hasNullValue() ? property.getString() : "");
            return inputEl;
        }

        availableSizeChanged() {
        }

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            var inputEl = <api.ui.text.TextInput>element;
            inputEl.onValueChanged((event: api.ui.ValueChangedEvent) => {

                var value = ValueTypes.LONG.newValue(event.getNewValue());
                listener(new api.form.inputtype.support.ValueChangedEvent(value));
            });
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            var inputEl: api.ui.text.TextInput = <api.ui.text.TextInput>occurrence;
            return ValueTypes.LONG.newValue(inputEl.getValue());
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            return value == null || api.util.StringHelper.isBlank(value.asString()) || !value.getType().equals(ValueTypes.LONG);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("Long", Long));
}
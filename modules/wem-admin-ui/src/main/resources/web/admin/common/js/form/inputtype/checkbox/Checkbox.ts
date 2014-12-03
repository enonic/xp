module api.content.form.inputtype.checkbox {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import BaseInputTypeSingleOccurrence = api.form.inputtype.support.BaseInputTypeSingleOccurrence;

    export class Checkbox extends BaseInputTypeSingleOccurrence<any,boolean> {

        private checkbox: api.ui.Checkbox;

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);

            this.checkbox = new api.ui.Checkbox();
        }

        getValueType(): ValueType {
            return ValueTypes.BOOLEAN;
        }

        newInitialValue(): Value {
            return ValueTypes.BOOLEAN.newFalse();
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {

            if (property.hasNonNullValue()) {
                this.checkbox.setChecked(property.getBoolean());
            }
            else {
                this.checkbox.setChecked(false);
            }

            this.appendChild(this.checkbox);
            this.checkbox.onValueChanged((event: api.ui.ValueChangedEvent) => {
                var newValue = ValueTypes.BOOLEAN.newValue(event.getNewValue());
                if (newValue) {
                    property.setValue(newValue);
                }
            });

            return wemQ<void>(null);
        }

        giveFocus(): boolean {
            return this.checkbox.giveFocus();
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            return new api.form.inputtype.InputValidationRecording();
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.checkbox.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.checkbox.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.checkbox.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.checkbox.unBlur(listener);
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("Checkbox", Checkbox));

}
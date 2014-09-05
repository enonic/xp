module api.content.form.inputtype.checkbox {

    import ValueTypes = api.data.type.ValueTypes;
    import BaseInputTypeSingleOccurrence = api.form.inputtype.support.BaseInputTypeSingleOccurrence;

    export class Checkbox extends BaseInputTypeSingleOccurrence<any> {

        private checkbox: api.ui.Checkbox;

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        newInitialValue(): api.data.Value {
            return ValueTypes.BOOLEAN.newFalse();
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.checkbox = new api.ui.Checkbox();

            if (properties[0] != null) {
                this.checkbox.setChecked(properties[0].getBoolean());

            }
            else {
                this.checkbox.setChecked(false);
            }


            this.appendChild(this.checkbox);
            this.checkbox.onValueChanged((event: api.ui.ValueChangedEvent) => {
                var newValue = ValueTypes.BOOLEAN.newValue(event.getNewValue());
                if (newValue) {
                    this.notifyValueChanged(new api.form.inputtype.ValueChangedEvent(newValue, 0));
                }
            });
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
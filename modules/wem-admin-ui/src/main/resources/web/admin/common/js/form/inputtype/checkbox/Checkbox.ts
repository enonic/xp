module api.content.form.inputtype.checkbox {

    import support = api.form.inputtype.support;

    export class Checkbox extends support.BaseInputTypeSingleOccurrence<any> {

        private checkbox: api.ui.Checkbox;

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        newInitialValue(): api.data.Value {
            return new api.data.Value('false', api.data.ValueTypes.STRING);
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
                var newValue = this.newValue(event.getNewValue());
                this.notifyValueChanged(new api.form.inputtype.ValueChangedEvent(newValue, 0));
            });
        }

        private newValue(s: string): api.data.Value {
            return new api.data.Value(s, api.data.ValueTypes.STRING);
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
module api.content.form.inputtype.checkbox {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import BaseInputTypeSingleOccurrence = api.form.inputtype.support.BaseInputTypeSingleOccurrence;
    import InputAlignment = api.ui.InputAlignment;

    export class Checkbox extends BaseInputTypeSingleOccurrence<boolean> {

        private checkbox: api.ui.Checkbox;

        private inputAlignment: InputAlignment = InputAlignment.LEFT;

        public static debug: boolean = false;

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
            this.readConfig(config.inputConfig);
        }

        private readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): void {
            if (inputConfig) {
                this.setInputAlignment(inputConfig['alignment']);
            }
        }

        private setInputAlignment(inputAlignmentObj: any) {
            if (inputAlignmentObj) {
                let inputAlignment: InputAlignment = InputAlignment[<string>inputAlignmentObj[0].value.toUpperCase()];
                this.inputAlignment = isNaN(inputAlignment) ? InputAlignment.LEFT : inputAlignment;
            }
        }

        getValueType(): ValueType {
            return ValueTypes.BOOLEAN;
        }

        newInitialValue(): Value {
            return ValueTypes.BOOLEAN.newBoolean(false);
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {
            let checked = property.hasNonNullValue() ? property.getBoolean() : false;
            this.checkbox =
                api.ui.Checkbox.create().setLabelText(input.getLabel()).setChecked(checked).setInputAlignment(this.inputAlignment).build();
            this.appendChild(this.checkbox);

            if (!ValueTypes.BOOLEAN.equals(property.getType())) {
                property.convertValueType(ValueTypes.BOOLEAN);
            }

            this.checkbox.onValueChanged((event: api.ValueChangedEvent) => {
                let newValue = ValueTypes.BOOLEAN.newValue(event.getNewValue());

                this.saveToProperty(newValue);
            });

            return wemQ<void>(null);
        }

        updateProperty(property: Property, unchangedOnly?: boolean): wemQ.Promise<void> {
            if (Checkbox.debug) {
                console.debug('Checkbox.updateProperty' + (unchangedOnly ? ' (unchanged only)' : ''), property);
            }
            if ((!unchangedOnly || !this.checkbox.isDirty()) && property.hasNonNullValue()) {
                this.checkbox.setChecked(property.getBoolean());
            }
            return wemQ<void>(null);
        }

        reset() {
            this.checkbox.resetBaseValues();
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

    api.form.inputtype.InputTypeManager.register(new api.Class('Checkbox', Checkbox));

}

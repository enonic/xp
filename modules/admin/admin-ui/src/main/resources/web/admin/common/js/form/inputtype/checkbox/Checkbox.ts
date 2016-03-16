module api.content.form.inputtype.checkbox {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import BaseInputTypeSingleOccurrence = api.form.inputtype.support.BaseInputTypeSingleOccurrence;

    export class Checkbox extends BaseInputTypeSingleOccurrence<boolean> {

        private checkbox: api.ui.Checkbox;

        public static debug: boolean = false;

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
        }

        getValueType(): ValueType {
            return ValueTypes.BOOLEAN;
        }

        newInitialValue(): Value {
            return ValueTypes.BOOLEAN.newBoolean(false);
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {
            var checked = property.hasNonNullValue() ? property.getBoolean() : false;
            this.checkbox = new api.ui.Checkbox(undefined, checked);
            this.appendChild(this.checkbox);

            if (!ValueTypes.BOOLEAN.equals(property.getType())) {
                property.convertValueType(ValueTypes.BOOLEAN);
            }

            this.checkbox.onValueChanged((event: api.ValueChangedEvent) => {
                var newValue = ValueTypes.BOOLEAN.newValue(event.getNewValue());

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
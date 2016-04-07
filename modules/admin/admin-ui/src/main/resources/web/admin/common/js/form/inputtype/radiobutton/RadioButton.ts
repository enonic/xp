module api.form.inputtype.radiobutton {

    import Option = api.ui.selector.Option;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import SelectedOptionsView = api.ui.selector.combobox.SelectedOptionsView;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;

    export class RadioButton extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<string> {

        private selector: api.ui.RadioGroup;
        private previousValidationRecording: api.form.inputtype.InputValidationRecording;
        private radioButtonOptions: {label: string; value: string;}[];

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config, "radio-button");
            this.readConfig(config.inputConfig);
        }

        private readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): void {
            var options: {label: string; value: string;}[] = [];

            var optionValues = inputConfig['option'] || [];
            var l = optionValues.length, optionValue;
            for (var i = 0; i < l; i++) {
                optionValue = optionValues[i];
                options.push({label: optionValue['value'], value: optionValue['@value']});
            }
            this.radioButtonOptions = options;
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return ValueTypes.STRING.newNullValue();
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {

            this.input = input;

            this.selector = this.createRadioElement(input.getName(), property);

            this.appendChild(this.selector);

            if (!ValueTypes.STRING.equals(property.getType())) {
                property.convertValueType(ValueTypes.STRING);
                if (!this.isValidOption(property.getString())) {
                    property.setValue(ValueTypes.STRING.newNullValue());
                }
            }

            return wemQ<void>(null);
        }

        updateProperty(property: api.data.Property, unchangedOnly: boolean): Q.Promise<void> {
            if ((!unchangedOnly || !this.selector.isDirty())) {
                this.selector.setValue(property.hasNonNullValue() ? property.getString() : "");
            }
            return wemQ<any>(null);
        }

        giveFocus(): boolean {
            return this.selector.giveFocus();
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            var recording = new api.form.inputtype.InputValidationRecording();
            var propertyValue = this.getProperty().getValue();
            if (propertyValue.isNull() && this.input.getOccurrences().getMinimum() > 0) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
                }
            }
            this.previousValidationRecording = recording;
            return recording;
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.selector.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.selector.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.selector.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.selector.unBlur(listener);
        }

        private createRadioElement(name: string, property: Property): api.ui.RadioGroup {

            var value = property.hasNonNullValue ? property.getString() : undefined;
            var radioGroup = new api.ui.RadioGroup(name, value);

            var options = this.radioButtonOptions;
            var l = options.length;
            for (var i = 0; i < l; i++) {
                var option = options[i];
                radioGroup.addOption(option.value, option.label);
            }

            radioGroup.onValueChanged((event: api.ValueChangedEvent)=> {
                this.saveToProperty(ValueTypes.STRING.newValue(event.getNewValue()));
            });


            return radioGroup;
        }

        private newValue(s: string): Value {
            return new Value(s, ValueTypes.STRING);
        }

        private isValidOption(value: string): boolean {
            var options = this.radioButtonOptions;
            var l = options.length;
            for (let i = 0; i < l; i++) {
                let option = options[i];
                if (option.value === value) {
                    return true;
                }
            }
            return false;
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("RadioButton", RadioButton));
}
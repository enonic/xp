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

        private selector: api.dom.Element;
        private property: Property;
        private previousValidationRecording: api.form.inputtype.InputValidationRecording;
        private radioButtonOptions: {label: string; value: string;}[];

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config, "radio-button");
            this.readConfig(config.inputConfig);
        }

        private readConfig(inputConfig: { [name: string]: string[]; }): void {
            var options: {label: string; value: string;}[] = [];

            var labels = inputConfig['option.label'] || [];
            var values = inputConfig['option.value'] || [];
            var l = Math.min(labels.length, values.length);
            for (var i = 0; i < l; i++) {
                options.push({label: labels[i], value: values[i]});
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
            this.property = property;

            this.selector = this.createRadioElement(input.getName(), property);

            this.appendChild(this.selector);

            return wemQ<void>(null);
        }

        giveFocus(): boolean {
            return this.selector.giveFocus();
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            var recording = new api.form.inputtype.InputValidationRecording();
            var propertyValue = this.property.getValue();
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

        private createRadioElement(name: string, property: Property): api.dom.Element {

            var radioGroup = new api.ui.RadioGroup(name);

            var options = this.radioButtonOptions;
            var l = options.length;
            for (var i = 0; i < l; i++) {
                var option = options[i];
                radioGroup.addOption(option.value, option.label);
            }

            if (property.hasNonNullValue()) {
                radioGroup.setValue(property.getString());
            }

            radioGroup.onValueChanged((event: api.ui.ValueChangedEvent)=> {
                property.setValue(this.newValue(event.getNewValue()));
                this.validate(false);
            });

            return radioGroup;
        }

        private newValue(s: string): Value {
            return new Value(s, ValueTypes.STRING);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("RadioButton", RadioButton));
}
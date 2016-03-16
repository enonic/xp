module api.form.inputtype.text {

    import support = api.form.inputtype.support;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class TextLine extends support.BaseInputTypeNotManagingAdd<string> {

        private regexpStr: string;
        private regexp: RegExp;

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
            this.readConfig(config.inputConfig);
        }

        private readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): void {
            var regexpConfig = inputConfig['regexp'] && inputConfig['regexp'][0];
            var regexp = regexpConfig && regexpConfig['value'];
            this.regexpStr = regexp || null;
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return new Value("", ValueTypes.STRING);
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            if (!ValueTypes.STRING.equals(property.getType())) {
                property.convertValueType(ValueTypes.STRING);
            }

            var inputEl = api.ui.text.TextInput.middle(undefined, property.getString());
            inputEl.setName(this.getInput().getName() + "-" + index);

            inputEl.onValueChanged((event: api.ValueChangedEvent) => {
                var isValid = this.isValid(event.getNewValue(), inputEl);
                if (isValid) {
                    var value = ValueTypes.STRING.newValue(event.getNewValue());
                    this.notifyOccurrenceValueChanged(inputEl, value);
                }
                inputEl.updateValidationStatusOnUserInput(isValid);
            });
            return inputEl;
        }


        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly: boolean) {
            var input = <api.ui.text.TextInput> occurrence;

            if (!unchangedOnly || !input.isDirty()) {
                input.setValue(property.getString());
            }
        }

        availableSizeChanged() {
        }

        private newValue(s: string): Value {
            return new Value(s, ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.STRING) ||
                   api.util.StringHelper.isBlank(value.getString());
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {
            var textInput = <api.ui.text.TextInput>inputElement;
            return this.isValid(textInput.getValue(), textInput, true);
        }

        private isValid(value: string, textInput: api.ui.text.TextInput, silent: boolean = false): boolean {
            var parent = textInput.getParentElement();
            if (!this.regexpStr || api.util.StringHelper.isEmpty(value)) {
                parent.removeClass('valid-regexp').removeClass('invalid-regexp');
                return true;
            }
            if (!this.regexp) {
                this.regexp = new RegExp(this.regexpStr);
            }
            var valid = this.regexp.test(value);
            if (!silent) {
                parent.toggleClass('valid-regexp', valid);
                parent.toggleClass('invalid-regexp', !valid);
            }
            return valid;
        }

        static getName(): api.form.InputTypeName {
            return new api.form.InputTypeName("TextLine", false);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class(TextLine.getName().getName(), TextLine));
}
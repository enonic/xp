module api.form.inputtype.text {

    import support = api.form.inputtype.support;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import i18n = api.util.i18n;
    import NumberHelper = api.util.NumberHelper;
    import DivEl = api.dom.DivEl;
    import FormInputEl = api.dom.FormInputEl;

    export class TextLine extends TextInputType {

        private regexp: RegExp;

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
            this.readConfig(config.inputConfig);
        }

        protected readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): void {
            super.readConfig(inputConfig);

            const regexpConfig = inputConfig['regexp'] ? inputConfig['regexp'][0] : {};
            const regexp = regexpConfig ? regexpConfig['value'] : '';
            this.regexp = new RegExp(regexp);

        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return super.newInitialValue() || new Value('', ValueTypes.STRING);
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            if (!ValueTypes.STRING.equals(property.getType())) {
                property.convertValueType(ValueTypes.STRING);
            }

            let inputEl = api.ui.text.TextInput.middle(undefined, property.getString());
            inputEl.setName(this.getInput().getName() + '-' + index);

            inputEl.onValueChanged((event: api.ValueChangedEvent) => {
                let isValid = this.isValid(event.getNewValue(), inputEl);
                let value = isValid ? ValueTypes.STRING.newValue(event.getNewValue()) : this.newInitialValue();
                this.notifyOccurrenceValueChanged(inputEl, value);
                inputEl.updateValidationStatusOnUserInput(isValid);

            });

            this.initOccurenceListeners(inputEl);

            return inputEl;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly: boolean) {
            let input = <api.ui.text.TextInput> occurrence;

            if (!unchangedOnly || !input.isDirty()) {
                input.setValue(property.getString());
            }
        }

        resetInputOccurrenceElement(occurrence: api.dom.Element) {
            let input = <api.ui.text.TextInput> occurrence;

            input.resetBaseValues();
        }

        availableSizeChanged() {
            // must be implemented by children
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.STRING) ||
                   api.util.StringHelper.isBlank(value.getString());
        }

        hasInputElementValidUserInput(inputElement: FormInputEl, recording?: api.form.inputtype.InputValidationRecording) {
            let textInput = <api.ui.text.TextInput>inputElement;
            return this.isValid(textInput.getValue(), textInput, true, recording);
        }

        protected isValid(value: string, textInput: api.ui.text.TextInput, silent: boolean = false,
                        recording?: api.form.inputtype.InputValidationRecording): boolean {
            let parent = textInput.getParentElement();

            if (api.util.StringHelper.isEmpty(value)) {
                parent.removeClass('valid-regexp invalid-regexp');
                return true;
            }

            const regexpValid = this.regexp.test(value);

            if (!silent) {
                parent.toggleClass('valid-regexp', regexpValid);
                parent.toggleClass('invalid-regexp', !regexpValid);
                parent.getEl().setAttribute('data-regex-status', i18n(`field.${regexpValid ? 'valid' : 'invalid'}`));
            }

            return regexpValid && super.isValid(value, textInput, silent, recording);
        }

        static getName(): api.form.InputTypeName {
            return new api.form.InputTypeName('TextLine', false);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class(TextLine.getName().getName(), TextLine));
}

module api.form.inputtype.text {

    import support = api.form.inputtype.support;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import i18n = api.util.i18n;
    import NumberHelper = api.util.NumberHelper;
    import DivEl = api.dom.DivEl;

    export class TextLine extends support.BaseInputTypeNotManagingAdd<string> {

        private regexp: RegExp;

        private maxLength: number;

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
            this.readConfig(config.inputConfig);
        }

        private readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): void {
            const regexpConfig = inputConfig['regexp'] ? inputConfig['regexp'][0] : {};
            const regexp = regexpConfig ? regexpConfig['value'] : '';
            this.regexp = new RegExp(regexp);

            const maxLengthConfig = inputConfig['max-length'] ? inputConfig['max-length'][0] : {};
            const maxLength = NumberHelper.toNumber(maxLengthConfig['value']);
            this.maxLength = maxLength > 0 ? maxLength : null;
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

                if (NumberHelper.isNumber(this.maxLength)) {
                    const lengthCounter = inputEl.getNextElement();
                    if (lengthCounter.hasClass('length-counter')) {
                        this.updateLengthCounterValue(lengthCounter, inputEl.getValue());
                    }
                }
            });

            inputEl.onRendered(() => {
                if (NumberHelper.isNumber(this.maxLength)) {

                    const lengthCounter = new DivEl('length-counter');
                    this.updateLengthCounterValue(lengthCounter, inputEl.getValue());

                    lengthCounter.insertAfterEl(inputEl);
                }
            });

            return inputEl;
        }

        private updateLengthCounterValue(lengthCounter: DivEl, newValue: string ) {
            lengthCounter.setHtml(`${this.maxLength - newValue.length}`);
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

        hasInputElementValidUserInput(inputElement: api.dom.Element, recording?: api.form.inputtype.InputValidationRecording) {
            let textInput = <api.ui.text.TextInput>inputElement;
            return this.isValid(textInput.getValue(), textInput, true, recording);
        }

        private isValid(value: string, textInput: api.ui.text.TextInput, silent: boolean = false,
                        recording?: api.form.inputtype.InputValidationRecording): boolean {
            let parent = textInput.getParentElement();

            if (api.util.StringHelper.isEmpty(value)) {
                parent.removeClass('valid-regexp invalid-regexp');
                return true;
            }

            const regexpValid = this.regexp.test(value);

            const lengthValid = this.isValidMaxLength(value);

            if (!lengthValid) {
                if (recording) {
                    recording.setAdditionalValidationRecord(
                        api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                            i18n('field.value.breaks.maxlength', this.maxLength)).build());
                }

            }

            if (!silent) {
                parent.toggleClass('valid-regexp', regexpValid);
                parent.toggleClass('invalid-regexp', !regexpValid);
                parent.getEl().setAttribute('data-regex-status', i18n(`field.${regexpValid ? 'valid' : 'invalid'}`));
            }

            return regexpValid && lengthValid;
        }

        private isValidMaxLength(value: string): boolean {
            return NumberHelper.isNumber(this.maxLength) ? value.length <= this.maxLength : true;
        }

        static getName(): api.form.InputTypeName {
            return new api.form.InputTypeName('TextLine', false);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class(TextLine.getName().getName(), TextLine));
}

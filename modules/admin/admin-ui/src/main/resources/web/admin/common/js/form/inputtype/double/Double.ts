module api.content.form.inputtype.double {

    import BaseInputTypeNotManagingAdd = api.form.inputtype.support.BaseInputTypeNotManagingAdd;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import TextInput = api.ui.text.TextInput;
    import NumberHelper = api.util.NumberHelper;
    import InputOccurrenceView = api.form.inputtype.support.InputOccurrenceView;
    import ValueTypeDouble = api.data.ValueTypeDouble;
    import i18n = api.util.i18n;

    export class Double extends BaseInputTypeNotManagingAdd<number> {

        private min: number = null;

        private max: number = null;

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
            this.readConfig(config);
        }

        getValueType(): ValueType {
            return ValueTypes.DOUBLE;
        }

        newInitialValue(): Value {
            return super.newInitialValue() || ValueTypes.DOUBLE.newNullValue();
        }

        protected readConfig(config: api.form.inputtype.InputTypeViewContext): void {
            this.min = this.getConfigProperty(config, 'min');
            this.max = this.getConfigProperty(config, 'max');
        }

        private getConfigProperty(config: api.form.inputtype.InputTypeViewContext, propertyName: string) {
            const configProperty = config.inputConfig[propertyName] ? config.inputConfig[propertyName][0] : {};
            return NumberHelper.toNumber(configProperty['value']);
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            if (!ValueTypes.DOUBLE.equals(property.getType())) {
                property.convertValueType(ValueTypes.DOUBLE);
            }

            let inputEl = api.ui.text.TextInput.middle(undefined, this.getPropertyValue(property));
            inputEl.setName(this.getInput().getName() + '-' + property.getIndex());

            inputEl.onValueChanged((event: api.ValueChangedEvent) => {
                let isValid = this.isValid(event.getNewValue());
                let value = isValid ? ValueTypes.DOUBLE.newValue(event.getNewValue()) : this.newInitialValue();

                this.notifyOccurrenceValueChanged(inputEl, value);
                inputEl.updateValidationStatusOnUserInput(isValid);
            });

            return inputEl;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly ?: boolean) {
            let input = <api.ui.text.TextInput> occurrence;

            if (!unchangedOnly || !input.isDirty()) {
                input.setValue(this.getPropertyValue(property));
            }
        }

        resetInputOccurrenceElement(occurrence: api.dom.Element) {
            let input = <api.ui.text.TextInput> occurrence;

            input.resetBaseValues();
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.DOUBLE);
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element, recording ?: api.form.inputtype.InputValidationRecording) {
            let value = <api.ui.text.TextInput>inputElement;

            return this.isValid(value.getValue(), recording);
        }

        private isValid(value: string, recording ?: api.form.inputtype.InputValidationRecording): boolean {

            if (api.util.StringHelper.isEmpty(value)) {
                return true;
            }

            if (api.util.NumberHelper.isNumber(+value)) {
                if (!this.isValidMin(NumberHelper.toNumber(value))) {
                    if (recording) {
                        recording.setAdditionalValidationRecord(
                            api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                                i18n('field.value.breaks.min', this.min)).build());
                    }

                    return false;
                }

                if (!this.isValidMax(NumberHelper.toNumber(value))) {
                    if (recording) {
                        recording.setAdditionalValidationRecord(
                            api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                                i18n('field.value.breaks.max', this.max)).build());
                    }

                    return false;
                }
            } else {
                return false;
            }
            return true;
        }

        private isValidMin(value: number) {
            if (NumberHelper.isNumber(value)) {
                if (NumberHelper.isNumber(this.min)) {
                    return value >= this.min;
                }
            }
            return true;
        }

        private isValidMax(value: number) {
            if (NumberHelper.isNumber(value)) {
                if (NumberHelper.isNumber(this.max)) {
                    return value <= this.max;
                }
            }
            return true;
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class('Double', Double));
}

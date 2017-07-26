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

            const minConfig = config.inputConfig['min'] ? config.inputConfig['min'][0] : {};
            this.min = NumberHelper.toNumber(minConfig['value']);

            const maxConfig = config.inputConfig['max'] ? config.inputConfig['max'][0] : {};
            this.max = NumberHelper.toNumber(maxConfig['value']);
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

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly?: boolean) {
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

        hasInputElementValidUserInput(inputElement: api.dom.Element) {
            let value = <api.ui.text.TextInput>inputElement;

            return this.isValid(value.getValue());
        }

        private isValid(value: string): boolean {

            if (api.util.StringHelper.isEmpty(value)) {
                return true;
            }

            if (api.util.NumberHelper.isNumber(+value)) {
                return this.isValidMax(api.util.NumberHelper.toNumber(value)) &&
                       this.isValidMin(api.util.NumberHelper.toNumber(value));
            }

            return false;
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

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            const recording = super.validate(silent);

            this.inputOccurrences.getOccurrenceViews().forEach((occurrenceView: InputOccurrenceView) => {

                const value = NumberHelper.toNumber((<api.ui.text.TextInput>occurrenceView.getInputElement()).getValue());

                if (!this.isValidMin(value)) {
                    recording.setAdditionalValidationRecord(
                        api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                            `The value cannot be less than ${this.min}`).build());
                }

                if (!this.isValidMax(value)) {
                    recording.setAdditionalValidationRecord(
                        api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                            `The value cannot be greater than ${this.max}`).build());
                }

            });

            if (!silent && recording.validityChanged(this.previousValidationRecording)) {
                this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));

                this.previousValidationRecording = recording;
            }

            return recording.clone();
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class('Double', Double));
}

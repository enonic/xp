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
    import Element = api.dom.Element;

    export abstract class TextInputType extends support.BaseInputTypeNotManagingAdd<string> {

        private maxLength: number;

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
            this.readConfig(config.inputConfig);
        }

        protected readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): void {
            const maxLengthConfig = inputConfig['max-length'] ? inputConfig['max-length'][0] : {};
            const maxLength = NumberHelper.toNumber(maxLengthConfig['value']);
            this.maxLength = 3;//maxLength > 0 ? maxLength : null;
        }

        protected initOccurenceListeners(inputEl: FormInputEl) {

            inputEl.onValueChanged((event: api.ValueChangedEvent) => {
                if (NumberHelper.isNumber(this.maxLength)) {
                    const lengthCounter = Element.fromHtmlElement(
                        (<HTMLElement>inputEl.getParentElement().getHTMLElement().querySelector('.length-counter')));
                    if (lengthCounter) {
                        this.updateLengthCounterValue(lengthCounter, inputEl.getValue());
                    }
                }
            });

            inputEl.onRendered(() => {
                if (NumberHelper.isNumber(this.maxLength)) {

                    const lengthCounter = new DivEl('length-counter');
                    this.updateLengthCounterValue(lengthCounter, inputEl.getValue());

                    inputEl.getParentElement().appendChild(lengthCounter);
                }
            });

            return inputEl;
        }

        private updateLengthCounterValue(lengthCounter: DivEl, newValue: string) {
            lengthCounter.setHtml(`${this.maxLength - newValue.length}`);
        }

        protected isValid(value: string, textInput: FormInputEl, silent: boolean = false,
                          recording?: api.form.inputtype.InputValidationRecording): boolean {
            const lengthValid = this.isValidMaxLength(value);

            if (!lengthValid) {
                if (recording) {
                    recording.setAdditionalValidationRecord(
                        api.form.AdditionalValidationRecord.create().setOverwriteDefault(true).setMessage(
                            i18n('field.value.breaks.maxlength', this.maxLength)).build());
                }

            }

            return lengthValid;
        }

        private isValidMaxLength(value: string): boolean {
            return NumberHelper.isNumber(this.maxLength) ? value.length <= this.maxLength : true;
        }
    }
}

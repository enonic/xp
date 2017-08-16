module api.content.form.inputtype.number {

    import BaseInputTypeNotManagingAdd = api.form.inputtype.support.BaseInputTypeNotManagingAdd;
    import NumberHelper = api.util.NumberHelper;
    import i18n = api.util.i18n;

    export abstract class NumberInputType extends BaseInputTypeNotManagingAdd<number> {

        private min: number = null;
        private max: number = null;

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
            this.readConfig(config);
        }

        protected readConfig(config: api.form.inputtype.InputTypeViewContext): void {
            this.min = this.getConfigProperty(config, 'min');
            this.max = this.getConfigProperty(config, 'max');
        }

        private getConfigProperty(config: api.form.inputtype.InputTypeViewContext, propertyName: string) {
            const configProperty = config.inputConfig[propertyName] ? config.inputConfig[propertyName][0] : {};
            return NumberHelper.toNumber(configProperty['value']);
        }

        protected isValid(value: string, recording ?: api.form.inputtype.InputValidationRecording): boolean {
            debugger;
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

                return true;
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
    }
}

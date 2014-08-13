module api.form {

    export class FormContext {

        private showEmptyFormItemSetOccurrences: boolean;

        constructor(builder: FormContextBuilder) {
            this.showEmptyFormItemSetOccurrences = builder.showEmptyFormItemSetOccurrences;
        }

        getShowEmptyFormItemSetOccurrences(): boolean {
            return this.showEmptyFormItemSetOccurrences;
        }

        createInputTypeViewConfig(inputTypeConfig: any, parentDataPath: api.data.DataPath,
                                  input: Input): api.form.inputtype.InputTypeViewConfig<any> {
            return <api.form.inputtype.InputTypeViewConfig<any>> {
                input: input,
                inputConfig: inputTypeConfig,
                parentDataPath: parentDataPath
            };
        }
    }

    export class FormContextBuilder {

        showEmptyFormItemSetOccurrences: boolean;

        public setShowEmptyFormItemSetOccurrences(value: boolean): FormContextBuilder {
            this.showEmptyFormItemSetOccurrences = value;
            return this;
        }

        public build(): FormContext {
            return new FormContext(this);
        }
    }
}
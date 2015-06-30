module api.form {

    export class InputBuilder {

        name: string;

        inputType: InputTypeName;

        label: string;

        immutable: boolean = false;

        occurrences: Occurrences;

        indexed: boolean = true;

        customText: string;

        validationRegex: string;

        helpText: string;

        inputTypeConfig: any;

        maximizeUIInputWidth: boolean;

        setName(value: string): InputBuilder {
            this.name = value;
            return this;
        }

        setInputType(value: InputTypeName): InputBuilder {
            this.inputType = value;
            return this;
        }

        setLabel(value: string): InputBuilder {
            this.label = value;
            return this;
        }

        setImmutable(value: boolean): InputBuilder {
            this.immutable = value;
            return this;
        }

        setOccurrences(value: Occurrences): InputBuilder {
            this.occurrences = value;
            return this;
        }

        setIndexed(value: boolean): InputBuilder {
            this.indexed = value;
            return this;
        }

        setCustomText(value: string): InputBuilder {
            this.customText = value;
            return this;
        }

        setValidationRegex(value: string): InputBuilder {
            this.validationRegex = value;
            return this;
        }

        setHelpText(value: string): InputBuilder {
            this.helpText = value;
            return this;
        }

        setInputTypeConfig(value: any): InputBuilder {
            this.inputTypeConfig = value;
            return this;
        }

        fromJson(json: json.InputJson): InputBuilder {
            this.name = json.name;
            this.inputType = InputTypeName.parseInputTypeName(json.inputType.name);
            this.label = json.label;
            this.immutable = json.immutable;
            this.occurrences = Occurrences.fromJson(json.occurrences);
            this.indexed = json.indexed;
            this.customText = json.customText;
            this.validationRegex = json.validationRegexp;
            this.helpText = json.helpText;
            this.inputTypeConfig = json.config;
            this.maximizeUIInputWidth = json.maximizeUIInputWidth;
            return this;
        }

        build(): Input {
            return new Input(this);
        }

    }

    /**
     * An input is a [[FormItem]] which the user can give input to.
     *
     * An input must be of certain type which using a [[InputTypeName]].
     * All input types must be registered in [[api.form.inputtype.InputTypeManager]] to be used.
     *
     */
    export class Input extends FormItem implements api.Equitable {

        private inputType: InputTypeName;

        private label: string;

        private immutable: boolean;

        private occurrences: Occurrences;

        private indexed: boolean;

        private customText: string;

        private validationRegex: string;

        private helpText: string;

        private inputTypeConfig: any;

        private maximizeUIInputWidth: boolean;

        constructor(builder: InputBuilder) {
            super(builder.name);
            this.inputType = builder.inputType;
            this.inputTypeConfig = builder.inputTypeConfig;
            this.label = builder.label;
            this.immutable = builder.immutable;
            this.occurrences = builder.occurrences;
            this.indexed = builder.indexed;
            this.customText = builder.customText;
            this.validationRegex = builder.validationRegex;
            this.helpText = builder.helpText;
            this.maximizeUIInputWidth = builder.maximizeUIInputWidth;
        }

        static fromJson(json: api.form.json.InputJson): Input {
            var builder = new InputBuilder();
            builder.fromJson(json);
            return builder.build();
        }

        getInputType(): InputTypeName {
            return this.inputType;
        }

        getLabel(): string {
            return this.label;
        }

        isImmutable(): boolean {
            return this.immutable;
        }

        getOccurrences(): Occurrences {
            return this.occurrences;
        }

        isIndexed(): boolean {
            return this.indexed;
        }

        isMaximizeUIInputWidth(): boolean {
            return this.maximizeUIInputWidth;
        }

        getCustomText(): string {
            return this.customText;
        }

        getValidationRegex(): string {
            return this.validationRegex;
        }

        getHelpText(): string {
            return this.helpText;
        }

        getInputTypeConfig(): any {
            return this.inputTypeConfig;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Input)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <Input>o;

            if (!api.ObjectHelper.equals(this.inputType, other.inputType)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.label, other.label)) {
                return false;
            }

            if (!api.ObjectHelper.booleanEquals(this.immutable, other.immutable)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.occurrences, other.occurrences)) {
                return false;
            }

            if (!api.ObjectHelper.booleanEquals(this.indexed, other.indexed)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.customText, other.customText)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.validationRegex, other.validationRegex)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.helpText, other.helpText)) {
                return false;
            }

            if (!api.ObjectHelper.anyEquals(this.inputTypeConfig, other.inputTypeConfig)) {
                return false;
            }

            return true;
        }

        public toInputJson(): api.form.json.FormItemTypeWrapperJson {

            return <api.form.json.FormItemTypeWrapperJson>{Input: <api.form.json.InputJson>{
                name: this.getName(),
                customText: this.getCustomText(),
                helpText: this.getHelpText(),
                immutable: this.isImmutable(),
                indexed: this.isIndexed(),
                label: this.getLabel(),
                occurrences: this.getOccurrences().toJson(),
                validationRegexp: this.getValidationRegex(),
                inputType: this.getInputType().toJson(),
                config: this.getInputTypeConfig(),
                maximizeUIInputWidth: this.isMaximizeUIInputWidth()
            }};
        }
    }
}
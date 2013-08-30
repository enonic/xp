module api_schema_content_form{

    export class Input extends FormItem {

        private inputType:InputType;

        private label:string;

        private immutable:boolean;

        private occurrences:Occurrences;

        private indexed:boolean;

        private customText:string;

        private validationRegex:string;

        private helpText:string;

        constructor(json) {

            super(json.name);
            this.inputType = new InputType(json.type);
            this.label = json.label;
            this.immutable = json.immutable;
            this.occurrences = new Occurrences(json.occurrences);
            this.indexed = json.indexed;
            this.customText = json.customText;
            this.validationRegex = json.validationRegexp;
            this.helpText = json.helpText;
        }

        getInputType():InputType {
            return this.inputType;
        }

        getLabel():string {
            return this.label;
        }

        isImmutable():boolean {
            return this.immutable;
        }

        getOccurrences():Occurrences {
            return this.occurrences;
        }

        isIndexed():boolean {
            return this.indexed;
        }

        getCustomText():string {
            return this.customText;
        }

        getValidationRegex():string {
            return this.validationRegex;
        }

        getHelpText():string {
            return this.helpText;
        }
    }
}
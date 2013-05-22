module API_schema_content_form{

    export class Input extends FormItem {

        private inputType:InputType;

        private label:string;

        private immutable:bool;

        private occurrences:Occurrences;

        private indexed:bool;

        private customText:string;

        private validationRegex:string;

        private helpText:string;

        constructor(json) {

            super(json.name);
            this.inputType = new InputType( json.type );
            this.label = json.label;
            this.immutable = json.immutable;
            this.occurrences = new Occurrences(json.occurrences);
            this.indexed = json.indexed;
            this.customText = json.customText;
            this.validationRegex = json.validationRegexp;
            this.helpText = json.helpText;
        }

        getLabel():string {
            return this.label;
        }

        isImmutable():bool {
            return this.immutable;
        }

        getOccurrences():Occurrences{
            return this.occurrences;
        }

        isIndexed():bool{
            return this.indexed;
        }

        getCustomText():string{
            return this.customText;
        }

        getValidationRegex():string{
            return this.validationRegex;
        }

        getHelpText():string{
            return this.helpText;
        }
    }
}
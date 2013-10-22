module api_schema_content_form_json{

    export class InputJson extends FormItemJson {

        customText:string;

        helpText:string;

        immutable:boolean;

        indexed:boolean;

        label:string;

        occurrences:OccurrencesJson;

        validationRegexp:string;

        inputType:InputTypeJson;
    }
}
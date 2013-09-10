module api_schema_content_form_json{

    export class InputJson extends FormItemJson {

        customText:string;

        helpText:string;

        immutable:boolean;

        indexed:boolean;

        label:string;

        occurrences:any;

        validationRegexp:string;

        inputType:InputTypeJson;
    }
}
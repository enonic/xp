module api.form.json{

    export class InputJson extends FormItemJson {

        customText:string;

        helpText:string;

        immutable:boolean;

        indexed:boolean;

        label:string;

        occurrences:OccurrencesJson;

        validationRegexp:string;

        inputType: string;

        config:any;

        maximizeUIInputWidth: boolean;
    }
}
module api_form_json{

    export class FormItemSetJson extends FormItemJson{

        customText:string;

        helpText:string;

        immutable:boolean;

        items:FormItemJson[];

        label:string;

        occurrences:OccurrencesJson;
    }
}
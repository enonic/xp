module api.form.json {

    export interface FormSetJson extends FormItemJson {

        helpText?: string;

        label: string;

        occurrences: OccurrencesJson;
    }
}
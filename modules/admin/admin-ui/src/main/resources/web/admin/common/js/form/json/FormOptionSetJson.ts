module api.form.json {

    export interface FormOptionSetJson extends FormItemJson {

        expanded: boolean;

        options: FormOptionSetOptionJson[];

        label: string;

        helpText: string;

        occurrences: OccurrencesJson;

        multiselection: OccurrencesJson;
    }
}
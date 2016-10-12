module api.form.json {

    export interface FormOptionSetJson extends FormItemJson {

        expanded: boolean;

        options: FormOptionSetOptionJson[];

        label: string;

        occurrences: OccurrencesJson;

        multiselection: OccurrencesJson;

        helpText: string;
    }
}
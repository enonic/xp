module api.form.json {

    export interface FormOptionSetJson extends FormSetJson {

        expanded?: boolean;

        options: FormOptionSetOptionJson[];

        multiselection: OccurrencesJson;
    }
}
module api.form.json {

    export interface FormItemSetJson extends FormItemJson {

        customText: string;

        helpText: string;

        immutable: boolean;

        items: FormItemTypeWrapperJson[];

        label: string;

        occurrences: OccurrencesJson;
    }
}
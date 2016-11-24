module api.form.json {

    export interface InputJson extends FormItemJson {

        customText?: string;

        helpText?: string;

        immutable?: boolean;

        indexed?: boolean;

        label: string;

        occurrences: OccurrencesJson;

        validationRegexp?: string;

        inputType: string;

        config?: any;

        maximizeUIInputWidth?: boolean;

        defaultValue?: {
            type: string;
            value: any;
        };

    }
}
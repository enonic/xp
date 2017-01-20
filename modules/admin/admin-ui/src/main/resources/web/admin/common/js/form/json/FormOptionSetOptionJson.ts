module api.form.json {

    export interface FormOptionSetOptionJson {

        name: string;

        label: string;

        defaultOption?: boolean;

        helpText?: string;

        items?: FormItemTypeWrapperJson[];
    }
}

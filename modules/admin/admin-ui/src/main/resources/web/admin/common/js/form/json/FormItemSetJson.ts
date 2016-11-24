module api.form.json {

    export interface FormItemSetJson extends FormSetJson {

        customText?: string;

        immutable?: boolean;

        items: FormItemTypeWrapperJson[];
    }
}
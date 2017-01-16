module api.form.json {

    export interface FieldSetJson extends LayoutJson {

        items: LayoutTypeWrapperJson[];

        label: string;
    }
}

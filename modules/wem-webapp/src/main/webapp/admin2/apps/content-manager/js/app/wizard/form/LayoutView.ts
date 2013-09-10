module app_wizard_form {

    export class LayoutView extends FormItemView {

        private layout:api_schema_content_form.Layout;

        constructor(layout:api_schema_content_form.Layout, idPrefix:string, className:string) {
            super(idPrefix, className, layout);

            this.layout = layout;
        }

        getData():api_data.Data[] {
            return null;
        }

    }
}
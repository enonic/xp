module app_wizard_form {

    export class FormItemContainer extends api_dom.DivEl {

        private formItem:api_schema_content_form.FormItem;

        constructor(formItem:api_schema_content_form.FormItem) {
            super("FormItemContainer");

            this.formItem = formItem;
        }

        getFormItem():api_schema_content_form.FormItem {
            return this.formItem;
        }

        getData():api_content_data.Data[] {
            throw new Error("Method needs to be implemented in inheritors");
        }
    }
}
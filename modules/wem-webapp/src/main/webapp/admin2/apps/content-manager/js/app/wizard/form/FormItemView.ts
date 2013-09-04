module app_wizard_form {

    export class FormItemView extends api_dom.DivEl {

        private formItem:api_schema_content_form.FormItem;

        constructor(idPrefix:string, className:string, formItem:api_schema_content_form.FormItem) {
            super(idPrefix, className);

            this.formItem = formItem;
        }

        getFormItem():api_schema_content_form.FormItem {
            return this.formItem;
        }

        getData():api_data.Data[] {
            throw new Error("Method needs to be implemented in inheritors");
        }
    }
}
module api_schema_content_form{

    export class FieldSet extends Layout {

        private label:string;

        private formItems:FormItem[] = [];

        constructor(fieldSetJson:api_schema_content_form_json.FieldSetJson) {
            super(fieldSetJson.name);
            this.label = fieldSetJson.label;

            if (fieldSetJson.items != null) {
                fieldSetJson.items.forEach((formItemJson:api_schema_content_form_json.FormItemJson) => {
                    this.addFormItem(FormItemFactory.createFormItem(formItemJson));
                });
            }
        }

        addFormItem(formItem:FormItem) {
            this.formItems.push(formItem);
        }

        getLabel():string {
            return this.label;
        }

        getFormItems():FormItem[] {
            return this.formItems;
        }
    }
}
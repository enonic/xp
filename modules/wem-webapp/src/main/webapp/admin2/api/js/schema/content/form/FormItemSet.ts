module api_schema_content_form{

    export class FormItemSet extends FormItem {

        private formItems:FormItem[] = [];

        constructor(name:string) {
            super(name);
        }

        addFormItem(formItem:FormItem) {
            this.formItems.push(formItem);
        }

        getFormItems():FormItem[] {
            return this.formItems;
        }
    }
}
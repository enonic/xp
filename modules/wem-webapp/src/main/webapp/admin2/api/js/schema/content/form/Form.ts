module api_schema_content_form{

    export class Form {

        private formItems:FormItem[] = [];

        constructor() {

        }

        addFormItem(formItem:FormItem) {
            this.formItems.push(formItem);
        }

        getFormItems():FormItem[] {
            return this.formItems;
        }
    }
}
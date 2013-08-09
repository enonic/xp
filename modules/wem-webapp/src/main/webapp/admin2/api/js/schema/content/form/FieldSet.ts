module api_schema_content_form{

    export class FieldSet extends Layout {

        private formItems:FormItem[] = [];

        constructor(name:string) {
            super(name);
        }

        addFormItem(formItem:FormItem) {
            this.formItems.push(formItem);
        }
    }
}
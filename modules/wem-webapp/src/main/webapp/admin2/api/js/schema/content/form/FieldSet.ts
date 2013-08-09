module api_schema_content_form{

    export class FieldSet extends Layout {

        private formItems:FormItem[] = [];

        static fromRemote(layout:api_remote_contenttype.Layout):FieldSet {

            return null;
        }

        constructor(name:string) {
            super(name);
        }

        addFormItem(formItem:FormItem) {
            this.formItems.push(formItem);
        }
    }
}
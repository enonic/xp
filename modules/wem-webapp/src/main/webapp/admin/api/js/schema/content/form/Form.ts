module api_schema_content_form{

    export class Form {

        private formItems:FormItem[] = [];

        private formItemByName:{[name:string] : FormItem; } = {};

        constructor() {

        }

        addFormItem(formItem:FormItem) {
            if (this.formItemByName[name]) {
                throw new Error("FormItem already added: " + name);
            }
            this.formItemByName[formItem.getName()] = formItem;
            this.formItems.push(formItem);
        }

        getFormItems():FormItem[] {
            return this.formItems;
        }

        getFormItemByName(name:string):FormItem {
            return this.formItemByName[name];
        }

        getInputByName(name:string):Input {
            return <Input>this.formItemByName[name];
        }

        toJson():api_schema_content_form_json.FormItemJson[] {

            return FormItem.formItemsToJson(this.getFormItems());
        }
    }
}
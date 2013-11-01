module api_form{

    export class Form {

        private formItems:FormItem[] = [];

        private formItemByName:{[name:string] : FormItem; } = {};

        constructor(formJson:api_form_json.FormJson) {

            formJson.formItems.forEach((formItemJson:api_form_json.FormItemJson) => {
                this.addFormItem(FormItemFactory.createFormItem(formItemJson));
            });
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

        toJson():api_form_json.FormJson {

            return <api_form_json.FormJson>{
                formItems: FormItem.formItemsToJson(this.getFormItems())
            }
        }
    }
}
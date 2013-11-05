module api_form{

    export class FormItem {

        private name:string;

        constructor(name:string) {
            this.name = name;
        }

        getName():string {
            return this.name;
        }

        public toFormItemJson():api_form_json.FormItemTypeWrapperJson {

            if (this instanceof Input) {
                return (<Input>this).toInputJson();
            }
            else if (this instanceof FormItemSet) {
                return (<FormItemSet>this).toFormItemSetJson();
            }
            else if (this instanceof Layout) {
                return (<Layout>this).toLayoutJson();
            }
            else {
                throw new Error("Unsupported FormItem: " + this);
            }
        }

        public static formItemsToJson(formItems:FormItem[]):api_form_json.FormItemTypeWrapperJson[] {

            var formItemArray:api_form_json.FormItemTypeWrapperJson[] = [];
            formItems.forEach((formItem:FormItem) => {
                formItemArray.push(formItem.toFormItemJson());
            });
            return formItemArray;
        }
    }
}
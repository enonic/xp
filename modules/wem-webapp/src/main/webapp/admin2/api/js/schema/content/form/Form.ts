module api_schema_content_form{

    export class Form {

        private formItems:FormItem[] = [];

        public static fromRemote(formItems:api_remote_contenttype.FormItem[]):Form {

            var form = new Form();

            formItems.forEach((remoteFormItem:api_remote_contenttype.FormItem, index:number) => {

                var formItem:FormItem;
                if (remoteFormItem.FormItemSet) {
                    formItem = FormItemSet.fromRemote(remoteFormItem.FormItemSet);
                }
                else if (remoteFormItem.Input) {
                    formItem = Input.fromRemote(remoteFormItem.Input);
                }
                else if (remoteFormItem.Layout) {
                    formItem = Layout.fromRemote(remoteFormItem.Layout);
                }

                if (formItem != null) {
                    form.addFormItem(formItem);
                }
            });

            return form;
        }

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
module api_schema_content_form{

    export class FormItemSet extends FormItem {

        private formItems:FormItem[] = [];

        public static fromRemote(remoteFormItemSet:api_remote_contenttype.FormItemSet):FormItemSet {

            var formItemSet = new FormItemSet(remoteFormItemSet.name);

            remoteFormItemSet.items.forEach((remoteFormItem:api_remote_contenttype.FormItem, index:number) => {

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
                    formItemSet.addFormItem(formItem);
                }
            });

            return formItemSet;
        }

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
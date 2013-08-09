module app_wizard {

    export class ContentTypeFactory {

        public static createForm(formItems:api_remote_contenttype.FormItem[]):api_schema_content_form.Form {

            var form = new api_schema_content_form.Form();

            formItems.forEach((remoteFormItem:api_remote_contenttype.FormItem, index:number) => {

                var formItem:api_schema_content_form.FormItem;
                if (remoteFormItem.FormItemSet) {
                    formItem = ContentTypeFactory.createFormItemSet(remoteFormItem.FormItemSet);
                }
                else if (remoteFormItem.Input) {
                    formItem = ContentTypeFactory.createInput(remoteFormItem.Input);
                }
                else if (remoteFormItem.Layout) {
                    formItem = ContentTypeFactory.createLayout(remoteFormItem.Layout);
                }

                if (formItem != null) {
                    form.addFormItem(formItem);
                }
            });

            return form;
        }

        public static createFormItemSet(remoteFormItemSet:api_remote_contenttype.FormItemSet):api_schema_content_form.FormItemSet {

            var formItemSet = new api_schema_content_form.FormItemSet(remoteFormItemSet.name);

            remoteFormItemSet.items.forEach((remoteFormItem:api_remote_contenttype.FormItem, index:number) => {

                var formItem:api_schema_content_form.FormItem;
                if (remoteFormItem.FormItemSet) {
                    formItem = ContentTypeFactory.createFormItemSet(remoteFormItem.FormItemSet);
                }
                else if (remoteFormItem.Input) {
                    formItem = ContentTypeFactory.createInput(remoteFormItem.Input);
                }
                else if (remoteFormItem.Layout) {
                    formItem = ContentTypeFactory.createLayout(remoteFormItem.Layout);
                }

                if (formItem != null) {
                    formItemSet.addFormItem(formItem);
                }
            });

            return formItemSet;
        }

        public static createInput(remoteInput:api_remote_contenttype.Input):api_schema_content_form.Input {

            var input = new api_schema_content_form.Input(remoteInput);
            return input;
        }

        public static createLayout(remoteLayout:api_remote_contenttype.Layout):api_schema_content_form.Layout {

            // TODO
            return null;
        }

        static createFieldSet(layout:api_remote_contenttype.Layout):api_schema_content_form.FieldSet {

            return null;
        }
    }
}
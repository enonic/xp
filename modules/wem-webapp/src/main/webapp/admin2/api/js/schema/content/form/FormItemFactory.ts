module api_schema_content_form{

    export class FormItemFactory {

        static createForm(formItems:api_schema_content_form_json.FormItemJson[]):Form {

            var form = new Form();
            formItems.forEach((formItemJson:api_schema_content_form_json.FormItemJson) => {
                form.addFormItem(FormItemFactory.createFormItem(formItemJson));
            });

            return form;
        }

        static createFormItem(formItemJson:api_schema_content_form_json.FormItemJson):FormItem {
            if (formItemJson.formItemType == "Input") {
                return FormItemFactory.createInput(<api_schema_content_form_json.InputJson>formItemJson);
            }
            else if (formItemJson.formItemType == "FormItemSet") {
                return FormItemFactory.createFormItemSet(<api_schema_content_form_json.FormItemSetJson>formItemJson);
            }
            else if (formItemJson.formItemType == "Layout") {
                // TODO: parse and create using a LayoutFactory
                return null;
            }
        }

        static createInput(inputJson:api_schema_content_form_json.InputJson):Input {
            return new Input(inputJson);
        }

        static createFormItemSet(formItemSetJson:api_schema_content_form_json.FormItemSetJson):FormItemSet {

            return new FormItemSet(formItemSetJson);
        }
    }
}
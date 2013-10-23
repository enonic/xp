module api_form{

    export class FormItemFactory {

        static createForm(formItems:api_form_json.FormItemJson[]):Form {

            var form = new Form();
            formItems.forEach((formItemJson:api_form_json.FormItemJson) => {
                form.addFormItem(FormItemFactory.createFormItem(formItemJson));
            });

            return form;
        }

        static createFormItem(formItemJson:api_form_json.FormItemJson):FormItem {
            if (formItemJson.formItemType == "Input") {
                return FormItemFactory.createInput(<api_form_json.InputJson>formItemJson);
            }
            else if (formItemJson.formItemType == "FormItemSet") {
                return FormItemFactory.createFormItemSet(<api_form_json.FormItemSetJson>formItemJson);
            }
            else if (formItemJson.formItemType == "Layout") {
                return FormItemFactory.createLayout(<api_form_json.LayoutJson>formItemJson);
            }
        }

        static createInput(inputJson:api_form_json.InputJson):Input {
            return new Input(inputJson);
        }

        static createFormItemSet(formItemSetJson:api_form_json.FormItemSetJson):FormItemSet {

            return new FormItemSet(formItemSetJson);
        }

        static createLayout(layoutJson:api_form_json.LayoutJson):Layout {
            if( layoutJson.layoutType == "FieldSet" ) {
                return FormItemFactory.createFieldSetLayout(<api_form_json.FieldSetJson>layoutJson);
            }
        }

        static createFieldSetLayout(fieldSetJson:api_form_json.FieldSetJson):FieldSet {
            return new FieldSet(fieldSetJson);
        }
    }
}
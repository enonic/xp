module api_form{

    export class FormItemFactory {

        static createForm(formJson:api_form_json.FormJson):Form {
            return new Form(formJson);
        }

        static createFormItem(formItemJson:api_form_json.FormItemTypeWrapperJson):FormItem {

            if (formItemJson.Input) {
                return FormItemFactory.createInput(<api_form_json.InputJson>formItemJson.Input);
            }
            else if (formItemJson.FormItemSet ) {
                return FormItemFactory.createFormItemSet(<api_form_json.FormItemSetJson>formItemJson.FormItemSet);
            }
            else if (formItemJson.FieldSet) {
                return FormItemFactory.createFieldSetLayout(<api_form_json.FieldSetJson>formItemJson.FieldSet);
            }

            console.log( "Unknown FormItem type: ", formItemJson );
            throw new Error("Unknown FormItem");
        }

        static createInput(inputJson:api_form_json.InputJson):Input {
            return new Input(inputJson);
        }

        static createFormItemSet(formItemSetJson:api_form_json.FormItemSetJson):FormItemSet {

            return new FormItemSet(formItemSetJson);
        }

        static createFieldSetLayout(fieldSetJson:api_form_json.FieldSetJson):FieldSet {
            return new FieldSet(fieldSetJson);
        }
    }
}
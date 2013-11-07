module api_form{

    export class FormItemFactory {

        static createForm(formJson:api_form_json.FormJson):Form {
            return new Form(formJson);
        }

        static createFormItem(formItemTypeWrapperJson:api_form_json.FormItemTypeWrapperJson):FormItem {

            if (formItemTypeWrapperJson.Input) {
                return FormItemFactory.createInput(<api_form_json.InputJson>formItemTypeWrapperJson.Input);
            }
            else if (formItemTypeWrapperJson.FormItemSet ) {
                return FormItemFactory.createFormItemSet(<api_form_json.FormItemSetJson>formItemTypeWrapperJson.FormItemSet);
            }
            else if (formItemTypeWrapperJson.FieldSet) {
                return FormItemFactory.createFieldSetLayout(<api_form_json.FieldSetJson>formItemTypeWrapperJson.FieldSet);
            }

            console.log( "Unknown FormItem type: ", formItemTypeWrapperJson );
            throw new Error("Unknown FormItem");
        }

        static createInput(inputJson:api_form_json.InputJson):Input {
            return Input.fromJson(inputJson);
        }

        static createFormItemSet(formItemSetJson:api_form_json.FormItemSetJson):FormItemSet {
            return new FormItemSet(formItemSetJson);
        }

        static createFieldSetLayout(fieldSetJson:api_form_json.FieldSetJson):FieldSet {
            return new FieldSet(fieldSetJson);
        }
    }
}
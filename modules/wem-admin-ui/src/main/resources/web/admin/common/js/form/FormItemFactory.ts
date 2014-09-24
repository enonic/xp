module api.form {

    export class FormItemFactory {

        static createForm(formJson: api.form.json.FormJson): Form {
            return Form.fromJson(formJson);
        }

        static createFormItem(formItemTypeWrapperJson: api.form.json.FormItemTypeWrapperJson): FormItem {

            if (formItemTypeWrapperJson.Input) {
                return FormItemFactory.createInput(<api.form.json.InputJson>formItemTypeWrapperJson.Input);
            }
            else if (formItemTypeWrapperJson.FormItemSet) {
                return FormItemFactory.createFormItemSet(<api.form.json.FormItemSetJson>formItemTypeWrapperJson.FormItemSet);
            }
            else if (formItemTypeWrapperJson.FieldSet) {
                return FormItemFactory.createFieldSetLayout(<api.form.json.FieldSetJson>formItemTypeWrapperJson.FieldSet);
            }

            console.log("Unknown FormItem type: ", formItemTypeWrapperJson);
            throw new Error("Unknown FormItem");
        }

        static createInput(inputJson: api.form.json.InputJson): Input {
            return Input.fromJson(inputJson);
        }

        static createFormItemSet(formItemSetJson: api.form.json.FormItemSetJson): FormItemSet {
            return new FormItemSet(formItemSetJson);
        }

        static createFieldSetLayout(fieldSetJson: api.form.json.FieldSetJson): FieldSet {
            return new FieldSet(fieldSetJson);
        }
    }
}
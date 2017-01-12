module api.form {

    export class FormItemFactory {

        static createForm(formJson: api.form.json.FormJson): Form {
            return Form.fromJson(formJson);
        }

        static createFormItem(formItemTypeWrapperJson: api.form.json.FormItemTypeWrapperJson): FormItem {

            if (formItemTypeWrapperJson.Input) {
                return FormItemFactory.createInput(<api.form.json.InputJson>formItemTypeWrapperJson.Input);
            } else if (formItemTypeWrapperJson.FormItemSet) {
                return FormItemFactory.createFormItemSet(<api.form.json.FormItemSetJson>formItemTypeWrapperJson.FormItemSet);
            } else if (formItemTypeWrapperJson.FieldSet) {
                return FormItemFactory.createFieldSetLayout(<api.form.json.FieldSetJson>formItemTypeWrapperJson.FieldSet);
            } else if (formItemTypeWrapperJson.FormOptionSet) {
                return FormItemFactory.createFormOptionSet(<api.form.json.FormOptionSetJson>formItemTypeWrapperJson.FormOptionSet);
            } else if (formItemTypeWrapperJson.FormOptionSetOption) {
                return FormItemFactory.createFormOptionSetOption(
                    <api.form.json.FormOptionSetOptionJson>formItemTypeWrapperJson.FormOptionSetOption);
            }

            console.error("Unknown FormItem type: ", formItemTypeWrapperJson);
            return null;
        }

        static createInput(inputJson: api.form.json.InputJson): Input {
            return Input.fromJson(inputJson);
        }

        static createFormItemSet(formItemSetJson: api.form.json.FormItemSetJson): api.form.FormItemSet {
            return new api.form.FormItemSet(formItemSetJson);
        }

        static createFieldSetLayout(fieldSetJson: api.form.json.FieldSetJson): FieldSet {
            return new FieldSet(fieldSetJson);
        }

        static createFormOptionSet(optionSetJson: api.form.json.FormOptionSetJson): api.form.FormOptionSet {
            return new api.form.FormOptionSet(optionSetJson);
        }

        static createFormOptionSetOption(optionSetOptionJson: api.form.json.FormOptionSetOptionJson): api.form.FormOptionSetOption {
            return new api.form.FormOptionSetOption(optionSetOptionJson);
        }
    }
}

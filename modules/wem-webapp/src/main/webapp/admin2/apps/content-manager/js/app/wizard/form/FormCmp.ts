module app_wizard_form {

    export class FormCmp extends api_ui.Panel {

        private form:api_schema_content_form.Form;

        private contentData:api_content_data.ContentData;

        constructor(form:api_schema_content_form.Form) {
            super("FormCmp");
            this.form = form;
            this.layout();
        }

        private layout() {

            this.form.getFormItems().forEach((formItem:api_schema_content_form.FormItem) => {

                if (formItem instanceof api_schema_content_form.FormItemSet) {

                }
                else if (formItem instanceof api_schema_content_form.Input) {

                    var input:api_schema_content_form.Input = <api_schema_content_form.Input>formItem;
                    this.contentData.getDataByName(input.getName());
                    var inputContainer = new InputContainer(input);
                }
            });
        }
    }
}
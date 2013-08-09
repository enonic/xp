module app_wizard_form {

    export class FormCmp extends api_ui.Panel {

        private form:api_schema_content_form.Form;

        private contentData:api_content_data.ContentData;

        constructor(form:api_schema_content_form.Form, contentData?:api_content_data.ContentData) {
            super("FormCmp");
            this.form = form;
            this.contentData = contentData;
            this.layout();
        }

        private layout() {

            console.log("FormCmp.layout() this.form: ", this.form);

            this.form.getFormItems().forEach((formItem:api_schema_content_form.FormItem) => {

                if (formItem instanceof api_schema_content_form.FormItemSet) {

                    var formItemSet:api_schema_content_form.FormItemSet = <api_schema_content_form.FormItemSet>formItem;

                    if (this.contentData != null) {

                        var dataSets:api_content_data.DataSet[] = this.contentData.getDataSetsByName(formItemSet.getName());
                        var formItemSetContainer = new FormItemSetContainer(formItemSet, dataSets);
                        this.appendChild(formItemSetContainer);
                    }
                    else {
                        var formItemSetContainer = new FormItemSetContainer(formItemSet);
                        this.appendChild(formItemSetContainer);
                    }

                }
                else if (formItem instanceof api_schema_content_form.Input) {

                    var input:api_schema_content_form.Input = <api_schema_content_form.Input>formItem;

                    if (this.contentData != null) {
                        var properties:api_content_data.Property[] = this.contentData.getPropertiesByName(input.getName());
                        var inputContainer = new InputContainer(input, properties);
                        this.appendChild(inputContainer);
                    }
                    else {
                        var inputContainer = new InputContainer(input);
                        this.appendChild(inputContainer);
                    }
                }
            });
        }
    }
}
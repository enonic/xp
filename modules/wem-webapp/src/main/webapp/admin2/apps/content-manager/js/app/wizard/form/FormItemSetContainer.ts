module app_wizard_form {

    export class FormItemSetContainer extends api_ui.Panel {

        private formItemSet:api_schema_content_form.FormItemSet;

        private dataSets:api_content_data.DataSet[];

        constructor(formItemSet:api_schema_content_form.FormItemSet, dataSets?:api_content_data.DataSet[]) {
            super("InputContainer");

            this.formItemSet = formItemSet;
            this.dataSets = dataSets;

            this.layout();
        }

        private layout() {

            var label = new FormItemSetLabel(this.formItemSet);
            this.appendChild(label);
        }
    }
}
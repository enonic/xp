module app_wizard_form {

    export class FormItemSetLabel extends api_dom.DivEl {

        private formItemSet:api_schema_content_form.FormItemSet;

        constructor(formItemSet:api_schema_content_form.FormItemSet) {
            super("FormItemSetLabel");

            this.formItemSet = formItemSet;

            this.getEl().setInnerHtml(this.formItemSet.getName());
        }
    }
}
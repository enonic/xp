module app_wizard_form {

    export class FieldSetLabel extends api_dom.DivEl {

        private fieldSet:api_schema_content_form.FieldSet;

        constructor(fieldSet:api_schema_content_form.FieldSet) {
            super("FieldSetLabel", "field-set-label");

            this.fieldSet = fieldSet;

            this.getEl().setInnerHtml(this.fieldSet.getLabel());
        }
    }
}
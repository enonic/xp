module api_form_layout {

    export class FieldSetLabel extends api_dom.DivEl {

        private fieldSet:api_form.FieldSet;

        constructor(fieldSet:api_form.FieldSet) {
            super("FieldSetLabel", "field-set-label");

            this.fieldSet = fieldSet;

            this.getEl().setInnerHtml(this.fieldSet.getLabel());
        }
    }
}
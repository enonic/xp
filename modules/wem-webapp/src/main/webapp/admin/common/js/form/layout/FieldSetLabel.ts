module api.form.layout {

    export class FieldSetLabel extends api.dom.DivEl {

        private fieldSet:api.form.FieldSet;

        constructor(fieldSet:api.form.FieldSet) {
            super("FieldSetLabel", "field-set-label");

            this.fieldSet = fieldSet;

            this.getEl().setInnerHtml(this.fieldSet.getLabel());
        }
    }
}
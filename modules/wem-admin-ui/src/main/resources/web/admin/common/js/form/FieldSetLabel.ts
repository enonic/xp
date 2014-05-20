module api.form {

    export class FieldSetLabel extends api.dom.DivEl {

        private fieldSet:FieldSet;

        constructor(fieldSet:FieldSet) {
            super("field-set-label");

            this.fieldSet = fieldSet;

            this.getEl().setInnerHtml(this.fieldSet.getLabel());
            this.getEl().setAttribute("title", this.fieldSet.getLabel());
        }
    }
}
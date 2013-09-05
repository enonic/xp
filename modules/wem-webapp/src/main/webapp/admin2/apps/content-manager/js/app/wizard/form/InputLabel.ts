module app_wizard_form {

    export class InputLabel extends api_dom.DivEl {

        private input:api_schema_content_form.Input;

        constructor(input:api_schema_content_form.Input) {
            super("InputLabel", "input-label");

            this.input = input;

            this.getEl().setInnerHtml(input.getLabel());
        }
    }
}
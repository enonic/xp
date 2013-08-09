module app_wizard_form {

    export class InputContainer extends api_ui.Panel {

        private input:api_schema_content_form.Input;

        constructor(input:api_schema_content_form.Input) {
            super("InputContainer");

            this.input = input;

            this.layout();
        }

        private layout() {

        }
    }
}
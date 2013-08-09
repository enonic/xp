module app_wizard_form {

    export class InputContainer extends api_ui.Panel {

        private input:api_schema_content_form.Input;

        private properties:api_content_data.Property[];

        constructor(input:api_schema_content_form.Input, properties?:api_content_data.Property[]) {
            super("InputContainer");

            this.input = input;
            this.properties = properties;

            this.layout();
        }

        private layout() {

            var label = new InputLabel(this.input);



            this.appendChild(label);
        }
    }
}
module admin.ui {

    export class BaseInputComponent {

        private input:API_content_schema_content_form.Input;

        constructor(input:API_content_schema_content_form.Input) {
            this.input = input;
        }

        getInput():API_content_schema_content_form.Input {
            return this.input;
        }
    }
}

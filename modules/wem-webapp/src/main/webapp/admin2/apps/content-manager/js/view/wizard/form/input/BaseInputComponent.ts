module admin.ui {

    export class BaseInputComponent {

        private input:API.content.schema.content.form.Input;

        constructor(input:API.content.schema.content.form.Input) {
            this.input = input;
        }

        getInput():API.content.schema.content.form.Input {
            return this.input;
        }
    }
}

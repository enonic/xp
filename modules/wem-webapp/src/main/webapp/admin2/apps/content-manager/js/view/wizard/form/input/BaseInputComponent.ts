module admin.ui {

    export class BaseInputComponent {

        private input:API_schema_content_form.Input;

        private values:any[];

        constructor(input:API_schema_content_form.Input) {
            this.input = input;
        }

        getInput():API_schema_content_form.Input {
            return this.input;
        }

        setValue(value:any, arrayIndex:number) {
            this.values[arrayIndex] = value;
        }
    }
}

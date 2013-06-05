module admin.ui {

    export class BaseInputComponent {

        private input:api_schema_content_form.Input;

        private values:any[];

        constructor(input:api_schema_content_form.Input) {
            this.input = input;
        }

        getInput():api_schema_content_form.Input {
            return this.input;
        }

        setValue(value:any, arrayIndex:number) {
            this.values[arrayIndex] = value;
        }
    }
}

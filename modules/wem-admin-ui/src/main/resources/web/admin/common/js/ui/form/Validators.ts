module api.ui.form {

    export class Validators {

        public static required(input: api.dom.FormInputEl): string {
            var value = input.getValue();
            return !value || value.length == 0 ? "Required field" : undefined;
        }

    }

}
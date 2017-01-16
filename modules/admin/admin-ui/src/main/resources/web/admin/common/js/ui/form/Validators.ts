module api.ui.form {

    export class Validators {

        public static required(input: api.dom.FormInputEl): string {
            let value = input.getValue();
            return api.util.StringHelper.isBlank(value) ? "This field is required" : undefined;
        }

        public static validEmail(input: api.dom.FormInputEl): string {
            let regexEmail = /\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/;
            let value = input.getValue();
            return !regexEmail.test(value) ? "Invalid email address" : undefined;
        }
    }

}

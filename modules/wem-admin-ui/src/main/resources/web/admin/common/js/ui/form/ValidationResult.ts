module api.ui.form {

    export class ValidationResult {

        private valid: boolean = true;
        private errors: ValidationError[] = [];

        constructor() {
        }

        addError(error: ValidationError) {
            this.errors.push(error);
            if (this.valid) {
                this.valid = false;
            }
        }

        isValid(): boolean {
            return this.valid;
        }

        getErrors(): ValidationError[] {
            return this.errors;
        }

    }

    export class ValidationError {
        private formItem: api.ui.form.FormItem;
        private message: string;

        constructor(formItem: api.ui.form.FormItem, message?: string) {
            this.formItem = formItem;
            this.message = message;
        }

        getFormItem(): api.ui.form.FormItem {
            return this.formItem;
        }

        getMessage(): string {
            return this.message;
        }
    }

}
module api.ui.form {

    export class FormItem extends api.dom.DivEl {

        private label: api.dom.LabelEl;
        private input: api.dom.FormInputEl;
        private error: api.dom.SpanEl;
        private validator: (input: api.dom.FormInputEl) => string;
        private invalidClass: string = "invalid";

        constructor(label: string, input: api.dom.FormInputEl) {
            super("form-input");
            this.error = new api.dom.SpanEl("error");
            this.label = new api.dom.LabelEl(label, input);
            this.input = input;
            this.appendChild(this.error);
            this.appendChild(this.label);
            this.appendChild(input);
        }

        getLabel(): api.dom.LabelEl {
            return this.label;
        }

        getInput(): api.dom.FormInputEl {
            return this.input;
        }

        validate(markInvalid?: boolean): string {
            var validationMessage;
            if (this.validator) {
                validationMessage = this.validator(this.input);
                if (markInvalid) {
                    if (validationMessage) {
                        this.addClass(this.invalidClass);
                    } else {
                        this.removeClass(this.invalidClass);
                    }
                    this.error.setHtml(validationMessage || "");
                }
            }
            return validationMessage;
        }

        setValidator(validator: (input: api.dom.FormInputEl) => string): FormItem {
            this.validator = validator;
            return this;
        }
    }
}
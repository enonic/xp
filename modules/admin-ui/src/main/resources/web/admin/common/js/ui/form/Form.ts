module api.ui.form {

    export class Form extends api.dom.DivEl {

        private formEl: api.dom.FormEl;

        private fieldsets: api.ui.form.Fieldset[] = [];

        private focusListeners: {(event: FocusEvent):void}[] = [];

        private blurListeners: {(event: FocusEvent):void}[] = [];

        constructor(className?: string) {
            super(className);
            this.formEl = new api.dom.FormEl("form");
            this.formEl.preventSubmit();

            this.appendChild(this.formEl);
        }

        add(fieldset: Fieldset) {
            fieldset.onFocus((event) => {
                this.notifyFocused(event);
            });
            fieldset.onBlur((event) => {
                this.notifyBlurred(event);
            });
            this.fieldsets.push(fieldset);
            this.formEl.appendChild(fieldset);
            return this;
        }

        validate(markInvalid?: boolean): ValidationResult {
            var validationResult: ValidationResult = new ValidationResult();
            this.fieldsets.forEach((fieldset: api.ui.form.Fieldset) => {
                fieldset.validate(validationResult, markInvalid);
            });
            return validationResult;
        }

        setFormData(data: any) {
            this.fieldsets.forEach((fieldset: api.ui.form.Fieldset) => {
                fieldset.setFieldsetData(data);
            });
        }

        getFormData(): any {
            var data = {};
            var fieldsetData;
            this.fieldsets.forEach((fieldset: api.ui.form.Fieldset) => {
                fieldsetData = fieldset.getFieldsetData();
                for (var property in fieldsetData) {
                    if (fieldsetData.hasOwnProperty(property)) {
                        data[property] = fieldsetData[property];
                    }
                }
            });
            return data;
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners.push(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners = this.focusListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners.push(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners = this.blurListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        notifyFocused(event: FocusEvent) {
            this.focusListeners.forEach((listener) => {
                listener(event);
            })
        }

        notifyBlurred(event: FocusEvent) {
            this.blurListeners.forEach((listener) => {
                listener(event);
            })
        }
    }
}
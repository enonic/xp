module api.ui.form {

    export class Form extends api.dom.DivEl {

        private formEl: api.dom.FormEl;

        private fieldsets: api.ui.form.Fieldset[] = [];

        constructor(className?: string) {
            super(className);
            this.formEl = new api.dom.FormEl("form");
            this.formEl.preventSubmit();

            this.appendChild(this.formEl);
        }

        add(fieldset: Fieldset) {
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
    }
}
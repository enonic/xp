module api.ui.form {

    export class Form extends api.ui.Panel {

        private formEl: api.dom.FormEl;

        private fieldsets: api.ui.form.Fieldset[] = [];

        constructor(generateId?: boolean, className?: string) {
            super(generateId, className);
            this.formEl = new api.dom.FormEl(false, "form");

            this.appendChild(this.formEl);
        }

        add(fieldset: Fieldset) {
            this.fieldsets.push(fieldset);
            this.formEl.appendChild(fieldset);
            return this;
        }

        validate(markInvalid?: boolean): string[] {
            var errors:string[] = [];
            this.fieldsets.forEach((fieldset: api.ui.form.Fieldset) => {
                var fieldsetErrors = fieldset.validate(markInvalid);
                if (fieldsetErrors.length > 0) {
                    errors = errors.concat(fieldsetErrors);
                }
            });
            return errors;
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
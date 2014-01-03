module api.ui.form {

    export class Form extends api.ui.Panel {

        private formEl:api.dom.FormEl;

        private inputs:api.dom.FormInputEl[];

        constructor(idPrefix?:string) {
            super(idPrefix);
            this.inputs = [];
            this.formEl = new api.dom.FormEl();
            this.formEl.getEl().addClass("form");

            this.appendChild(this.formEl);
        }

        fieldset(fieldset:Fieldset) {
            this.formEl.appendChild(fieldset);
            return this;
        }

        registerInput(input:api.dom.FormInputEl) {
            this.inputs.push(input);
        }

        setFormData(data:any) {
            this.inputs.forEach((input:api.dom.FormInputEl) => {
                var inputValue = data[input.getName()];
                if (inputValue) {
                    input.setValue(inputValue);
                }
            })
        }

        getFormData():any {
            var data = {};
            this.inputs.forEach((input:api.dom.FormInputEl) => {
                data[input.getName()] = input.getValue();
            });
            return data;
        }
    }
}
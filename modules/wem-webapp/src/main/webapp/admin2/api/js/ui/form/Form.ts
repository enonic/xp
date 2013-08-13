module api_ui_form {
    export class Form extends api_ui.Panel {

        private formEl:api_dom.FormEl;

        private inputs:api_dom.FormInputEl[];

        constructor(id?:string) {
            super(id);
            this.inputs = [];
            this.formEl = new api_dom.FormEl();
            this.formEl.getEl().addClass("form");

            this.appendChild(this.formEl);
        }

        fieldset(fieldset:Fieldset) {
            this.formEl.appendChild(fieldset);
            return this;
        }

        addInput(input:api_dom.FormInputEl) {
            this.inputs.push(input);
        }

        setFormData(data:any) {
            this.inputs.forEach((input:api_dom.FormInputEl) => {
                var inputValue = data[input.getName()];
                if (inputValue) {
                    input.setValue(inputValue);
                }
            })
        }

        getFormData():any {
            var data = {};
            this.inputs.forEach((input:api_dom.FormInputEl) => {
                data[input.getName()] = input.getValue();
            });
            return data;
        }
    }
}
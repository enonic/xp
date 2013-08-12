module api_ui {
    export class Form extends api_ui.Panel {

        private formEl:api_dom.FormEl;

        private inputs:api_ui.FormInput[];

        constructor(id?:string) {
            super(id);
            this.inputs = [];
            this.formEl = new api_dom.FormEl();

            this.appendChild(this.formEl);
        }

        fieldset(fieldset:api_ui.Fieldset) {
            this.formEl.appendChild(fieldset);
            return this;
        }

        addInput(input:api_ui.FormInput) {
            this.inputs.push(input);
        }

        setFormData(data:any) {
            this.inputs.forEach((input:api_ui.FormInput) => {
                var inputValue = data[input.getName()];
                if (inputValue) {
                    input.setValue(inputValue);
                }
            })
        }

        getFormData():any {
            var data = {};
            this.inputs.forEach((input:api_ui.FormInput) => {
                data[input.getName()] = input.getValue();
            });
            return data;
        }
    }
}
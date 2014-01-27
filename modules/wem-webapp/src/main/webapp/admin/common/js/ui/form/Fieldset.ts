module api.ui.form {
    export class Fieldset extends api.dom.FieldsetEl {

        private legend: api.dom.LegendEl;

        private items: api.ui.form.FormItem[] = [];

        constructor(legend?: string) {
            super();
            if (legend) {
                this.legend = new api.dom.LegendEl(legend);
                this.appendChild(this.legend);
            }
        }

        add(formItem: FormItem) {
            this.items.push(formItem);
            this.appendChild(formItem);
        }

        validate(validationResult:ValidationResult, markInvalid?: boolean) {
            this.items.forEach((item: api.ui.form.FormItem) => {
                item.validate(validationResult, markInvalid);
            });
        }

        setFieldsetData(data: any) {
            var input, inputValue;
            this.items.forEach((item: api.ui.form.FormItem) => {
                input = item.getInput();
                inputValue = data[input.getName()];
                if (inputValue) {
                    input.setValue(inputValue);
                }
            });
        }

        getFieldsetData(): any {
            var input, data = {};
            this.items.forEach((item: api.ui.form.FormItem) => {
                input = item.getInput();
                data[input.getName()] = input.getValue();
            });
            return data;
        }
    }
}
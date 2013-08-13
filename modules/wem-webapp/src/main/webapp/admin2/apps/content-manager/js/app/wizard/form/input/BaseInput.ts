module app_wizard_form_input {

    export class BaseInput extends api_dom.DivEl implements Input {

        private input:api_schema_content_form.Input;

        private inputs:api_dom.FormInputEl[];

        constructor(idPrefix:string) {
            super(idPrefix);
        }

        getHTMLElement():HTMLElement {
            return super.getHTMLElement();
        }

        layout(input:api_schema_content_form.Input, properties?:api_content_data.Property[]) {

            this.input = input;
            this.inputs = [];

            if (properties.length > 0) {
                properties.forEach((property:api_content_data.Property, index:number) => {

                    var inputEl = this.createInputEl(index, property);
                    this.inputs[index] = inputEl;
                    this.appendChild(inputEl);
                });
            }
            else {
                var inputEl = this.createInputEl(0);

                this.inputs[0] = inputEl;
                this.appendChild(inputEl);

                if (this.input.getOccurrences().getMinimum() > 0) {
                    for (var i = 1; i < this.input.getOccurrences().getMinimum(); i++) {

                        var nextInputEl = this.createInputEl(i);
                        this.inputs[i] = nextInputEl;
                        this.appendChild(nextInputEl);
                    }
                }
            }
        }

        createInputEl(index:number, property?:api_content_data.Property):api_dom.FormInputEl {
            throw new Error("Must be implemented by inheritor");
        }

        getValues():string[] {

            var values:string[] = [];
            this.inputs.forEach((input:api_ui.TextArea) => {
                values.push(input.getValue());
            });
            return values;
        }
    }
}
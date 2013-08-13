module app_wizard_form_input {

    export class TextArea extends api_dom.DivEl implements Input {

        private input:api_schema_content_form.Input;

        private inputs:api_dom.FormInputEl[];

        constructor() {
            super("TextArea");
        }

        getHTMLElement():HTMLElement {
            return super.getHTMLElement();
        }

        layout(input:api_schema_content_form.Input, properties?:api_content_data.Property[]) {

            this.input = input;
            this.inputs = [];

            console.log("TextArea.layout properties: ", properties);

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

        private createInputEl(index:number, property?:api_content_data.Property):api_dom.FormInputEl {
            var inputEl = new api_ui.TextArea(this.input.getName() + "-" + index);
            //inputEl.setName(this.input.getName());
            if (property != null) {
                inputEl.setValue(property.getValue());
            }
            return inputEl;
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
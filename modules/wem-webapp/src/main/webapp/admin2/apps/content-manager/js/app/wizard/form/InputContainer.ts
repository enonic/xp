module app_wizard_form {

    export class InputContainer extends FormItemContainer {

        private input:api_schema_content_form.Input;

        private parentDataSet:api_content_data.DataSet;

        private properties:api_content_data.Property[];

        private inputCmp;

        constructor(input:api_schema_content_form.Input, parentDataSet:api_content_data.DataSet) {
            super(input);

            this.input = input;
            this.parentDataSet = parentDataSet;

            this.properties = this.parentDataSet.getPropertiesByName(input.getName());

            this.layout();
        }

        private layout() {

            var label = new InputLabel(this.input);
            this.appendChild(label);

            var inputType = this.input.getInputType();
            if (inputType.isBuiltIn()) {
                var newInput = window["app_wizard_form_input"][inputType.getName()];
                if( newInput == null ){
                    throw new Error("No built-in component for input type found: " + inputType.getName());
                }
                var newInputPrototype = Object.create(newInput.prototype);
                newInputPrototype.constructor.apply(newInputPrototype);
            }
            else {
                throw Error("Custom input types are not supported yet: " + inputType.getName());
            }

            this.inputCmp = newInputPrototype;
            this.inputCmp.layout(this.input, this.properties);
            this.getEl().appendChild(this.inputCmp.getHTMLElement());
        }

        getData():api_content_data.Data[] {
            return this.getPropeties();
        }

        getPropeties():api_content_data.Property[] {

            var properties:api_content_data.Property[] = [];
            this.inputCmp.getValues().forEach((value:string, index:number) => {
                properties[index] = new api_content_data.Property(this.input.getName(), value, "TEXT");
            });
            return properties;
        }
    }
}
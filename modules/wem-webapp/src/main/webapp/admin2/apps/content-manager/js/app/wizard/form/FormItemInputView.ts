module app_wizard_form {

    export class FormItemInputView extends FormItemView {

        private input:api_schema_content_form.Input;

        private parentDataSet:api_data.DataSet;

        private properties:api_data.Property[];

        private inputCmp;

        constructor(input:api_schema_content_form.Input, parentDataSet:api_data.DataSet) {
            super(input);

            this.input = input;
            this.parentDataSet = parentDataSet;

            this.properties = this.parentDataSet.getPropertiesByName(input.getName());

            this.layout();
        }

        private layout() {

            var label = new InputLabel(this.input);
            this.appendChild(label);

            var inputType:api_schema_content_form.InputTypeName = this.input.getInputType();
            if (inputType.isBuiltIn()) {
                var newInput = window["app_wizard_form_input"][inputType.getName()];
                if (newInput == null) {
                    throw new Error("No built-in component for input type found: " + inputType.getName());
                }
                var newInputPrototype = Object.create(newInput.prototype);
                newInputPrototype.constructor.apply(newInputPrototype);
            }
            else {

                // custom types must register it self by name in a global known input type registry

                throw Error("Custom input types are not supported yet: " + inputType.getName());
            }

            this.inputCmp = newInputPrototype;
            this.inputCmp.layout(this.input, this.properties);
            this.getEl().appendChild(this.inputCmp.getHTMLElement());
        }

        getData():api_data.Data[] {
            return this.getProperties();
        }

        getProperties():api_data.Property[] {

            var properties:api_data.Property[] = [];
            this.inputCmp.getValues().forEach((value:string, index:number) => {
                properties[index] = new api_data.Property(this.input.getName(), value, "TEXT");
            });
            return properties;
        }
    }
}
module app_wizard_form {

    export class InputContainerView extends FormItemView {

        private input:api_schema_content_form.Input;

        private properties:api_data.Property[];

        private inputView;

        constructor(input:api_schema_content_form.Input, properties?:api_data.Property[]) {
            super("InputContainerView", "input-container-view", input);

            this.input = input;
            this.properties = properties != null ? properties : [];

            this.layout();
        }

        private layout() {

            var label = new InputLabel(this.input);
            this.appendChild(label);

            var inputType:api_schema_content_form.InputTypeName = this.input.getInputType();
            if (inputType.isBuiltIn()) {
                var newInput = window["app_wizard_form_input"][inputType.getName()];
                if (newInput == null) {
                    newInput = window["app_wizard_form_input"]["NoInputTypeFound"];
                }
                var newInputPrototype = Object.create(newInput.prototype);
                newInputPrototype.constructor.apply(newInputPrototype);
            }
            else {

                // custom types must register it self by name in a global known input type registry

                throw Error("Custom input types are not supported yet: " + inputType.getName());
            }

            this.inputView = newInputPrototype;
            this.inputView.layout(this.input, this.properties);
            this.getEl().appendChild(this.inputView.getHTMLElement());
        }

        getData():api_data.Data[] {
            return this.getProperties();
        }

        getProperties():api_data.Property[] {

            var properties:api_data.Property[] = [];
            this.inputView.getValues().forEach((value:string, index:number) => {
                properties[index] = new api_data.Property(this.input.getName(), value, "TEXT");
            });
            return properties;
        }
    }
}
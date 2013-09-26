module app_wizard_form_input {

    export class InputView extends app_wizard_form.FormItemView {

        private input:api_schema_content_form.Input;

        private properties:api_data.Property[];

        private inputView:app_wizard_form_input_type.BaseInputTypeView;

        constructor(input:api_schema_content_form.Input, properties?:api_data.Property[]) {
            super("InputView", "input-view", input);

            this.input = input;
            this.properties = properties != null ? properties : [];

            this.doLayout();
        }

        private doLayout() {

            var label = new InputLabel(this.input);
            this.appendChild(label);

            var inputType:api_schema_content_form.InputTypeName = this.input.getInputType();
            if (inputType.isBuiltIn()) {
                var newInputPrototype;
                if (InputTypeManager.isRegistered(inputType.getName())) {
                    newInputPrototype = InputTypeManager.createView(inputType.getName());
                } else {
                    newInputPrototype = InputTypeManager.createView("NoInputTypeFound");
                }
            }
            else {

                // custom types must register it self by name using InputTypeManager.register(name, class);

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
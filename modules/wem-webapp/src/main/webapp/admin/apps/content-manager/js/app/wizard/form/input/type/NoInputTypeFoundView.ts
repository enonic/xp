module app_wizard_form_input_type {

    export class NoInputTypeFoundView extends BaseInputTypeView {

        constructor() {
            super("NoInputTypeFound");
        }

        layout(input:api_form.Input, properties?:api_data.Property[]) {

            var divEl = new api_dom.DivEl();
            divEl.getEl().setInnerHtml("Warning: no input type found: " + input.getInputType().toString());

            super.layout(input, properties);
        }

        createInputOccurrenceElement(index:number, property:api_data.Property):api_dom.Element {

            var inputEl = api_ui.TextInput.middle(this.getInput().getName() + "-" + index);
            inputEl.setName(this.getInput().getName());
            if (property != null) {
                inputEl.setValue(property.getString());
            }
            return inputEl;
        }

        getValue(occurrence:api_dom.Element):api_data.Value {
            var inputEl = <api_ui.TextInput>occurrence;
            return new api_data.Value(inputEl.getValue(), api_data.ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value:api_data.Value):boolean {
            // TODO:
            return false;
        }
    }

    app_wizard_form_input.InputTypeManager.register("NoInputTypeFound", NoInputTypeFoundView);
}
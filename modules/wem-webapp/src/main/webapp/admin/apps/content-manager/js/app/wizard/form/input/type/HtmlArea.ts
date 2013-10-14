module app_wizard_form_input_type {

    export class HtmlArea extends BaseInputTypeView {

        constructor() {
            super("HtmlArea");
        }

        createInputOccurrenceElement(index:number, property:api_data.Property):api_dom.Element {

            var textAreaEl = new api_ui.TextArea(this.getInput().getName() + "-" + index);
            if (property != null) {
                textAreaEl.setValue(property.getString());
            }
            return textAreaEl;
        }

        getValue(occurrence:api_dom.Element):api_data.Value {
            var inputEl = <api_ui.TextArea>occurrence;
            return new api_data.Value(inputEl.getValue(), api_data.ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value:api_data.Value):boolean {
            // TODO:
            return false;
        }
    }

    app_wizard_form_input.InputTypeManager.register("HtmlArea", HtmlArea);
}
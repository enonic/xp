module app_wizard_form_input {

    export class TextLine extends BaseInputTypeView {

        constructor() {
            super("TextLine");
        }

        createInputOccurrenceElement(index:number, property?:api_data.Property):api_dom.Element {

            var inputEl = api_ui.TextInput.middle(this.getInput().getName() + "-" + index);
            inputEl.setName(this.getInput().getName());
            if (property != null) {
                inputEl.setValue(property.getValue());
            }
            return inputEl;
        }

        getValue(occurrence:api_dom.Element):string {
            var inputEl = <api_ui.TextInput>occurrence;
            return inputEl.getValue();
        }
    }
}
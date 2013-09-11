module app_wizard_form_input {

    export class TextArea extends BaseInputTypeView {

        constructor() {
            super("TextArea");
        }

        createInputOccurrence(index:number, property?:api_data.Property):api_dom.Element {

            var inputEl = new api_ui.TextArea(this.getInput().getName() + "-" + index);
            if (property != null) {
                inputEl.setValue(property.getValue());
            }
            return inputEl;
        }

        getValue(occurrence:api_dom.Element):string {
            var textAreaEl = <api_ui.TextArea>occurrence;
            return textAreaEl.getValue();
        }
    }
}
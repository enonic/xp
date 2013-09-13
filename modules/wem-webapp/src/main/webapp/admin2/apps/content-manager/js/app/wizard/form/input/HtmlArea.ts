module app_wizard_form_input {

    export class HtmlArea extends BaseInputTypeView {

        constructor() {
            super("HtmlArea");
        }

        createInputOccurrenceElement(index:number, property?:api_data.Property):api_dom.Element {

            var textAreaEl = new api_ui.TextArea(this.getInput().getName() + "-" + index);
            if (property != null) {
                textAreaEl.setValue(property.getValue());
            }
            return textAreaEl;
        }

        getValue(occurrence:api_dom.Element):string {
            var textAreaEl = <api_ui.TextArea>occurrence;
            return textAreaEl.getValue();
        }
    }
}
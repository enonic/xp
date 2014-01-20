module api.form.inputtype.text {

    export class HtmlArea extends api.form.inputtype.support.BaseInputTypeView {

        constructor() {
            super();
        }

        createInputOccurrenceElement(index:number, property:api.data.Property):api.dom.Element {

            var textAreaEl = new api.ui.TextArea(this.getInput().getName() + "-" + index);
            if (property != null) {
                textAreaEl.setValue(property.getString());
            }
            return textAreaEl;
        }

        getValue(occurrence:api.dom.Element):api.data.Value {
            var inputEl = <api.ui.TextArea>occurrence;
            return new api.data.Value(inputEl.getValue(), api.data.ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value:api.data.Value):boolean {
            if (api.util.isStringBlank(value.asString())) {
                return true;
            } else {
                return false;
            }
        }
    }

    api.form.input.InputTypeManager.register("HtmlArea", HtmlArea);
}
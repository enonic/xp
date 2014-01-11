module api.form.inputtype.text {

    export class Region extends api.form.inputtype.support.BaseInputTypeView {

        constructor() {
            super(true);
        }

        createInputOccurrenceElement(index:number, property:api.data.Property):api.dom.Element {

            var inputEl = api.ui.TextInput.middle(this.getInput().getName() + "-" + index);
            inputEl.setName(this.getInput().getName());
            if (property != null) {
                inputEl.setValue(property.getString());
            }
            return inputEl;
        }

        getValue(occurrence:api.dom.Element):api.data.Value {
            var inputEl = <api.ui.TextInput>occurrence;
            return new api.data.Value(inputEl.getValue(), api.data.ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value:api.data.Value):boolean {
            // TODO:
            return true;
        }
    }

    api.form.input.InputTypeManager.register("Region", TextLine);
}
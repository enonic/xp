module api.form.input.support {

    export class NoInputTypeFoundView extends api.form.inputtype.support.BaseInputTypeView {

        constructor() {
            super();
        }

        layout(input:api.form.Input, properties?:api.data.Property[]) {

            var divEl = new api.dom.DivEl();
            divEl.getEl().setInnerHtml("Warning: no input type found: " + input.getInputType().toString());

            super.layout(input, properties);
        }

        createInputOccurrenceElement(index:number, property:api.data.Property):api.dom.Element {

            var inputEl = api.ui.TextInput.middle();
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
            return false;
        }
    }

    api.form.input.InputTypeManager.register("NoInputTypeFound", NoInputTypeFoundView);
}
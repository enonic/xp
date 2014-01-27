module api.form.inputtype.text {

    import support = api.form.inputtype.support;

    export class TextLine extends support.BaseInputTypeView {

        constructor() {
            super();
        }

        createInputOccurrenceElement(index:number, property:api.data.Property):api.dom.Element {

            var inputEl = api.ui.TextInput.middle();
            inputEl.setName(this.getInput().getName());
            if (property != null) {
                inputEl.setValue(property.getString());
            }
            inputEl.addListener({
                onValueChanged: (event:any) => {
                                    var validationRecorder:api.form.ValidationRecorder = new api.form.ValidationRecorder();
                                    this.validate(validationRecorder);
                                    if (this.validityChanged(validationRecorder)) {
                                        this.notifyValidityChanged(new support.ValidityChangedEvent(validationRecorder.valid()));
                                    }
                                }
            });
            return inputEl;
        }

        getValue(occurrence:api.dom.Element):api.data.Value {
            var inputEl = <api.ui.TextInput>occurrence;
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

    api.form.input.InputTypeManager.register("TextLine", TextLine);
}
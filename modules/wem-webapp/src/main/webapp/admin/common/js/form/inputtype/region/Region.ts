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
            inputEl.addListener({
                                    onValueChanged: (oldValue:string, newValue:string) => {
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
            if (api.util.isStringBlank(value.asString()))
            {
                return true;
            } else {
                return false;
            }
        }
    }

    api.form.input.InputTypeManager.register("Region", TextLine);
}
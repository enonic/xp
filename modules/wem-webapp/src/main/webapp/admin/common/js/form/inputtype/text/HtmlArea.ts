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
            textAreaEl.addListener( "valuechange", (event:any) => {
                var validationRecorder:api.form.ValidationRecorder = new api.form.ValidationRecorder();
                this.validate(validationRecorder);
                if (this.validityChanged(validationRecorder)) {
                    this.notifyListeners("validitychange", {isValid: validationRecorder.valid()});
                }
            });
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
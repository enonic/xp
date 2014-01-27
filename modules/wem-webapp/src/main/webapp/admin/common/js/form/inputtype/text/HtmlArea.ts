module api.form.inputtype.text {

    import support = api.form.inputtype.support;

    export class HtmlArea extends support.BaseInputTypeView {

        constructor() {
            super();
        }

        createInputOccurrenceElement(index:number, property:api.data.Property):api.dom.Element {

            var textAreaEl = new api.ui.TextArea(this.getInput().getName() + "-" + index);
            if (property != null) {
                textAreaEl.setValue(property.getString());
            }
            textAreaEl.onValueChanged( (event:any) => {
                var validationRecorder:api.form.ValidationRecorder = new api.form.ValidationRecorder();
                this.validate(validationRecorder);
                if (this.validityChanged(validationRecorder)) {
                    this.notifyValidityChanged(new support.ValidityChangedEvent( validationRecorder.valid()));
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
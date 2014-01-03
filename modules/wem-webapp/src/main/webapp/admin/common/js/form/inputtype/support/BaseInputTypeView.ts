module api.form.inputtype.support {

    export class BaseInputTypeView extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private input:api.form.Input;

        private inputOccurrences:InputOccurrences;

        constructor(idPrefix:string) {
            super(idPrefix, "input-type-view");
        }

        getHTMLElement():HTMLElement {
            return super.getHTMLElement();
        }

        isManagingAdd():boolean {
            return false;
        }

        addFormItemOccurrencesListener(listener:api.form.FormItemOccurrencesListener) {
            this.inputOccurrences.addListener(listener);
        }

        removeFormItemOccurrencesListener(listener:api.form.FormItemOccurrencesListener) {
            this.inputOccurrences.removeListener(listener);
        }

        public maximumOccurrencesReached():boolean {
            return this.inputOccurrences.maximumOccurrencesReached();
        }

        createAndAddOccurrence() {
            this.inputOccurrences.createAndAddOccurrence();
        }

        layout(input:api.form.Input, properties:api.data.Property[]) {

            this.input = input;
            this.inputOccurrences = new InputOccurrences(this, this.input, properties);
            this.inputOccurrences.layout();
        }

        getValues():api.data.Value[] {

            return this.inputOccurrences.getValues();
        }

        getAttachments():api.content.attachment.Attachment[] {
            return [];
        }

        validate(validationRecorder:api.form.ValidationRecorder) {

            this.getValues().forEach((value:api.data.Value, index:number) => {
                if (this.valueBreaksRequiredContract(value)) {
                    validationRecorder.registerBreaksRequiredContract(new api.data.DataId(this.input.getName(), index))
                }
            });
        }

        getInput():api.form.Input {
            return this.input;
        }

        valueBreaksRequiredContract(value:api.data.Value):boolean {
            throw new Error("Must be implemented by inheritor");
        }

        createInputOccurrenceElement(index:number, property:api.data.Property):api.dom.Element {
            throw new Error("Must be implemented by inheritor");
        }

        getValue(occurrence:api.dom.Element):api.data.Value {
            throw new Error("Must be implemented by inheritor");
        }

        giveFocus(): boolean {
            if( this.inputOccurrences ) {
                return this.inputOccurrences.giveFocus();
            }
            return false;
        }
    }
}
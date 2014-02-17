module api.form.inputtype.support {

    export class InputOccurrenceView extends api.form.FormItemOccurrenceView implements api.event.Observable {

        private inputOccurrence: InputOccurrence;

        private inputElement: api.dom.Element;

        private removeButtonEl: api.dom.AEl;

        private dragControl: api.dom.DivEl;

        private requiredContractBroken: boolean;

        constructor(inputOccurrence: InputOccurrence, baseInputTypeView: BaseInputTypeView, property: api.data.Property) {
            super("input-occurrence-view", inputOccurrence);

            var inputElement = baseInputTypeView.createInputOccurrenceElement(inputOccurrence.getIndex(), property);

            this.requiredContractBroken = baseInputTypeView.valueBreaksRequiredContract(property != null ? property.getValue() : null);

            baseInputTypeView.addOnValueChangedListener(inputElement, (event: api.form.inputtype.support.ValueChangedEvent) => {

                console.log("InputOccurrenceView value changed from [" + event.getOldValue().asString() + "] to [" + event.getNewValue().asString() + "]");

                var newStateOfRequiredContractBroken = baseInputTypeView.valueBreaksRequiredContract(event.getNewValue());

                if (this.requiredContractBroken != newStateOfRequiredContractBroken) {
                    this.requiredContractBroken = newStateOfRequiredContractBroken;
                    baseInputTypeView.notifyRequiredContractBroken(newStateOfRequiredContractBroken, inputOccurrence.getIndex());
                }
            });

            this.inputOccurrence = inputOccurrence;

            this.dragControl = new api.dom.DivEl("drag-control");
            this.appendChild(this.dragControl);

            this.removeButtonEl = new api.dom.AEl("remove-button");
            this.appendChild(this.removeButtonEl);
            this.removeButtonEl.setClickListener(() => {
                this.notifyRemoveButtonClicked();
            });

            var inputWrapper = new api.dom.DivEl("input-wrapper");
            this.appendChild(inputWrapper);

            this.inputElement = inputElement;
            inputWrapper.appendChild(this.inputElement);

            this.refresh();
        }

        refresh() {

            if (this.inputOccurrence.oneAndOnly()) {
                this.addClass("single-occurrence").removeClass("multiple-occurrence");
            }
            else {
                this.addClass("multiple-occurrence").removeClass("single-occurrence");
            }

            this.getEl().setData("dataId", this.inputOccurrence.getDataId().toString());
        }

        getIndex(): number {
            return this.inputOccurrence.getIndex();
        }

        getInputElement(): api.dom.Element {
            return this.inputElement;
        }

        giveFocus(): boolean {
            return this.inputElement.giveFocus();
        }
    }
}
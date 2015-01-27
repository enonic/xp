module api.form.inputtype.support {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import PropertyValueChangedEvent = api.data.PropertyValueChangedEvent;

    export class InputOccurrenceView extends api.form.FormItemOccurrenceView {

        private inputOccurrence: InputOccurrence;

        private inputElement: api.dom.Element;

        private removeButtonEl: api.dom.AEl;

        private dragControl: api.dom.DivEl;

        private requiredContractBroken: boolean;

        constructor(inputOccurrence: InputOccurrence, baseInputTypeView: BaseInputTypeNotManagingAdd<any,any>, property: Property) {
            super("input-occurrence-view", inputOccurrence);

            var inputElement = baseInputTypeView.createInputOccurrenceElement(inputOccurrence.getIndex(), property);

            this.requiredContractBroken = baseInputTypeView.valueBreaksRequiredContract(property != null ? property.getValue() : null);

            var propertyValueChangedHandler = (event: PropertyValueChangedEvent) => {

                var newStateOfRequiredContractBroken = baseInputTypeView.valueBreaksRequiredContract(event.getNewValue());

                if (this.requiredContractBroken != newStateOfRequiredContractBroken) {
                    this.requiredContractBroken = newStateOfRequiredContractBroken;
                    baseInputTypeView.notifyRequiredContractBroken(newStateOfRequiredContractBroken, inputOccurrence.getIndex());
                }
            };
            property.onPropertyValueChanged(propertyValueChangedHandler);
            

            this.inputOccurrence = inputOccurrence;

            this.dragControl = new api.dom.DivEl("drag-control");
            this.appendChild(this.dragControl);

            this.removeButtonEl = new api.dom.AEl("remove-button");
            this.appendChild(this.removeButtonEl);
            this.removeButtonEl.onClicked((event: MouseEvent) => {
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

        onFocus(listener: (event: FocusEvent) => void) {
            this.inputElement.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.inputElement.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.inputElement.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.inputElement.unBlur(listener);
        }
    }
}
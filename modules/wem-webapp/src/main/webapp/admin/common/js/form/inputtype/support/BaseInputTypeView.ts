module api.form.inputtype.support {

    export class BaseInputTypeView extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private input: api.form.Input;

        private inputOccurrences: InputOccurrences;

        private listeners: {[eventName:string]:{(event: InputTypeEvent):void}[]} = {};

        private previousValidationRecording: api.form.ValidationRecording;

        constructor(className?: string) {
            super("input-type-view" + ( className ? " " + className : ""));
            this.listeners[InputTypeEvents.ValidityChanged] = [];

            jQuery(this.getHTMLElement()).sortable({
                axis: "y",
                containment: 'parent',
                handle: '.drag-control',
                tolerance: 'pointer',
                update: (event, ui) => {

                    var occurrenceOrderAccordingToDOM = this.resolveOccurrencesInOrderAccordingToDOM();

                    // Update index of each occurrence
                    occurrenceOrderAccordingToDOM.forEach((occurrence: InputOccurrence, index: number) => {
                        occurrence.setIndex(index);
                    });

                    this.inputOccurrences.sortOccurrences((a: InputOccurrence, b: InputOccurrence) => {
                        return a.getIndex() - b.getIndex();
                    });
                }
            });
        }

        private resolveOccurrencesInOrderAccordingToDOM(): InputOccurrence[] {

            var childCount = this.getHTMLElement().children.length;
            var occurrenceOrderAccordingToDOM: InputOccurrence[] = [];
            for (var i = 0; i < childCount; i++) {
                var child: Element = this.getHTMLElement().children[i];
                occurrenceOrderAccordingToDOM[i] = this.inputOccurrences.getOccurrences().filter((occ: InputOccurrence) => {
                    return occ.getDataId().toString() == child.getAttribute('data-dataid');
                })[0];
            }

            return occurrenceOrderAccordingToDOM;
        }

        onElementAddedToParent(parent: api.dom.Element) {
            super.onElementAddedToParent(parent);
            this.addFormItemOccurrencesListener({
                onOccurrenceAdded: () => {
                    jQuery(this.getHTMLElement()).sortable("refresh");
                },
                onOccurrenceRemoved: () => {
                }
            });
        }

        getHTMLElement(): HTMLElement {
            return super.getHTMLElement();
        }

        isManagingAdd(): boolean {
            return false;
        }

        addFormItemOccurrencesListener(listener: api.form.FormItemOccurrencesListener) {
            this.inputOccurrences.addListener(listener);
        }

        removeFormItemOccurrencesListener(listener: api.form.FormItemOccurrencesListener) {
            this.inputOccurrences.removeListener(listener);
        }

        private addListener(eventName: InputTypeEvents, listener: (event: InputTypeEvent)=>void) {
            this.listeners[eventName].push(listener);
        }

        onValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.addListener(InputTypeEvents.ValidityChanged, listener);
        }

        private removeListener(eventName: InputTypeEvents, listener: (event: InputTypeEvent)=>void) {
            this.listeners[eventName].filter((currentListener: (event: InputTypeEvent)=>void) => {
                return listener == currentListener;
            });
        }

        unValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.removeListener(InputTypeEvents.ValidityChanged, listener);
        }

        private notifyListeners(eventName: InputTypeEvents, event: InputTypeEvent) {
            this.listeners[eventName].forEach((listener: (event: InputTypeEvent)=>void) => {
                listener(event);
            });
        }

        public maximumOccurrencesReached(): boolean {
            return this.inputOccurrences.maximumOccurrencesReached();
        }


        createAndAddOccurrence() {
            this.inputOccurrences.createAndAddOccurrence();
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.input = input;
            this.inputOccurrences = new InputOccurrences(this, this.input, properties);
            this.inputOccurrences.layout();
            jQuery(this.getHTMLElement()).sortable("refresh");
        }

        getValues(): api.data.Value[] {

            return this.inputOccurrences.getValues();
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return [];
        }

        validate(silent: boolean = true): api.form.ValidationRecording {

            var recording = new api.form.ValidationRecording();
            var numberOfValids = 0;
            this.inputOccurrences.getOccurrenceViews().forEach((occurrenceView: InputOccurrenceView) => {

                var value = this.getValue(occurrenceView.getInputElement());
                var breaksRequiredContract = this.valueBreaksRequiredContract(value);
                if (!breaksRequiredContract) {
                    numberOfValids++;
                }
            });

            if (numberOfValids < this.input.getOccurrences().getMinimum()) {
                recording.breaksMinimumOccurrences(this.input.getPath());
            }
            if (this.input.getOccurrences().maximumBreached(numberOfValids)) {
                recording.breaksMaximumOccurrences(this.input.getPath());
            }

            //console.log("BaseInputView[" + this.input.getPath().toString() + "].validate:");
            //recording.print();

            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    console.log(".. validity changed - notifying");
                    this.notifyValidityChanged(new ValidityChangedEvent(recording, this.input.getPath()));
                }
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        notifyRequiredContractBroken(state: boolean, index: number) {

            this.validate(false);
        }

        notifyValidityChanged(event: ValidityChangedEvent) {

            console.log("Validity changed for [" + this.input.getPath().toString() + "] ->  " + event.isValid());
            this.notifyListeners(InputTypeEvents.ValidityChanged, event);
        }

        getInput(): api.form.Input {
            return this.input;
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            throw new Error("Must be implemented by inheritor");
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {
            throw new Error("Must be implemented by inheritor");
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            throw new Error("Must be implemented by inheritor");
        }

        addOnValueChangedListener(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            throw new Error("Must be implemented by inheritor");
        }

        giveFocus(): boolean {
            if (this.inputOccurrences) {
                return this.inputOccurrences.giveFocus();
            }
            return false;
        }

        addEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            // Adapter for InputTypeView method, to be implemented on demand in inheritors
        }

        removeEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            // Adapter for InputTypeView method, to be implemented on demand in inheritors
        }
    }
}

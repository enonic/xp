module api.form.inputtype.support {

    export class BaseInputTypeView<CONFIG> extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private config: api.form.inputtype.InputTypeViewConfig<CONFIG>;

        private input: api.form.Input;

        private inputOccurrences: InputOccurrences;

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        constructor(config: api.form.inputtype.InputTypeViewConfig<CONFIG>, className?: string) {
            super("input-type-view" + ( className ? " " + className : ""));
            api.util.assertNotNull(config, "config cannt be null");
            this.config = config;

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

            this.onAdded((event) => {
                this.onOccurrenceAdded(() => {
                    jQuery(this.getHTMLElement()).sortable("refresh");
                });
            });
        }

        availableSizeChanged(newWidth: number, newHeight:number) {

        }

        public getConfig(): api.form.inputtype.InputTypeViewConfig<CONFIG> {
            return this.config;
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

        getElement(): api.dom.Element {
            return this;
        }

        isManagingAdd(): boolean {
            return false;
        }

        onOccurrenceAdded(listener: (event: api.form.OccurrenceAddedEvent)=>void) {
            this.inputOccurrences.onOccurrenceAdded(listener);
        }

        onOccurrenceRemoved(listener: (event: api.form.OccurrenceRemovedEvent)=>void) {
            this.inputOccurrences.onOccurrenceRemoved(listener);
        }

        unOccurrenceAdded(listener: (event: api.form.OccurrenceAddedEvent)=>void) {
            this.inputOccurrences.unOccurrenceAdded(listener);
        }

        unOccurrenceRemoved(listener: (event: api.form.OccurrenceRemovedEvent)=>void) {
            this.inputOccurrences.unOccurrenceRemoved(listener);
        }

        onValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.filter((currentListener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValidityChanged(event: api.form.inputtype.InputValidityChangedEvent) {
            this.inputValidityChangedListeners.forEach((listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
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

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            var recording = new api.form.inputtype.InputValidationRecording();
            var numberOfValids = 0;
            this.inputOccurrences.getOccurrenceViews().forEach((occurrenceView: InputOccurrenceView) => {

                var value = this.getValue(occurrenceView.getInputElement());
                var breaksRequiredContract = this.valueBreaksRequiredContract(value);
                if (!breaksRequiredContract) {
                    numberOfValids++;
                }
            });

            if (numberOfValids < this.input.getOccurrences().getMinimum()) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (this.input.getOccurrences().maximumBreached(numberOfValids)) {
                recording.setBreaksMaximumOccurrences(true);
            }

            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
                }
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        notifyRequiredContractBroken(state: boolean, index: number) {

            this.validate(false);
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

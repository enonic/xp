module api.form.inputtype.support {

    export class BaseInputTypeView extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private input: api.form.Input;

        private inputOccurrences: InputOccurrences;

        private listeners: {[eventName:string]:{(event:any):void}[]} = {};

        private previousErrors:api.form.ValidationRecorder;

        constructor(className?: string) {
            super("input-type-view" + ( className ? " " + className : ""));
            this.listeners[InputTypeEvents.ValidityChange] = [];

            jQuery(this.getHTMLElement()).sortable({
                axis: "y",
                containment: 'parent',
                handle: '.drag-control',
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

        addListener(eventName:InputTypeEvents, listener:(event:any)=>void) {
            this.listeners[eventName].push(listener);
        }

        removeListener(eventName:InputTypeEvents, listener:(event:any)=>void) {
            this.listeners[eventName].filter((currentListener:(event:any)=>void) => {
                return listener == currentListener;
            });
        }

        notifyListeners(eventName:InputTypeEvents, event:any) {
            this.listeners[eventName].forEach((listener:(event:any)=>void) => {
                listener(event);
            });
        }


        validityChanged(validationRecorder:api.form.ValidationRecorder):boolean {
            var validityChanged:boolean = this.previousErrors == null || this.previousErrors.valid() != validationRecorder.valid();
            this.previousErrors = validationRecorder;
            return validityChanged;
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

        validate(validationRecorder: api.form.ValidationRecorder) {

            this.getValues().forEach((value: api.data.Value, index: number) => {
                if (this.valueBreaksRequiredContract(value)) {
                    validationRecorder.registerBreaksRequiredContract(new api.data.DataId(this.input.getName(), index))
                }
            });
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

        giveFocus(): boolean {
            if (this.inputOccurrences) {
                return this.inputOccurrences.giveFocus();
            }
            return false;
        }
    }
}

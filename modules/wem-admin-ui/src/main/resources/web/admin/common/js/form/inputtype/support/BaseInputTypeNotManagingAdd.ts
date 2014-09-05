module api.form.inputtype.support {

    export class BaseInputTypeNotManagingAdd<CONFIG> extends api.dom.DivEl implements api.form.inputtype.InputTypeViewNotManagingAdd {

        private config: api.form.inputtype.InputTypeViewContext<CONFIG>;

        private input: api.form.Input;

        private inputOccurrences: InputOccurrences;

        private valueAddedListeners: {(event: api.form.inputtype.ValueAddedEvent) : void}[] = [];

        private valueChangedListeners: {(event: api.form.inputtype.ValueChangedEvent) : void}[] = [];

        private valueRemovedListeners: {(event: api.form.inputtype.ValueRemovedEvent) : void}[] = [];

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        /**
         * The index of child Data being dragged.
         */
        private draggingIndex: number;

        constructor(config: api.form.inputtype.InputTypeViewContext<CONFIG>, className?: string) {
            super("input-type-view" + ( className ? " " + className : ""));
            api.util.assertNotNull(config, "config cannt be null");
            this.config = config;

            wemjq(this.getHTMLElement()).sortable({
                axis: "y",
                containment: 'parent',
                handle: '.drag-control',
                tolerance: 'pointer',
                start: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStart(event, ui),
                update: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDUpdate(event, ui)
            });
        }

        private handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {

            var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
            this.draggingIndex = draggedElement.getSiblingIndex();

            ui.placeholder.html("Drop form item set here");
        }

        private handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            if (this.draggingIndex >= 0) {
                var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
                var draggedToIndex = draggedElement.getSiblingIndex();
                this.handleMovedOccurrence(this.draggingIndex, draggedToIndex);
            }

            this.draggingIndex = -1;
        }

        private handleMovedOccurrence(index: number, destinationIndex: number) {

            this.inputOccurrences.moveOccurrence(index, destinationIndex);
        }

        availableSizeChanged() {

        }

        public getConfig(): api.form.inputtype.InputTypeViewContext<CONFIG> {
            return this.config;
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
            this.inputOccurrences = new InputOccurrences(<InputOccurrencesConfig>{
                baseInputTypeView: this,
                input: this.input,
                properties: properties
            });

            this.inputOccurrences.onValueAdded((event: api.form.inputtype.ValueAddedEvent) => {
                this.notifyValueAdded(event);
            });

            this.inputOccurrences.onValueChanged((event: api.form.inputtype.ValueChangedEvent) => {
                this.notifyValueChanged(event);
            });

            this.inputOccurrences.onValueRemoved((event: api.form.inputtype.ValueRemovedEvent) => {
                this.notifyValueRemoved(event);
            });

            this.onAdded((event: api.dom.ElementAddedEvent) => {
                this.onOccurrenceAdded(() => {
                    wemjq(this.getHTMLElement()).sortable("refresh");
                });
            });

            this.inputOccurrences.layout();
            wemjq(this.getHTMLElement()).sortable("refresh");
        }


        onFocus(listener: (event: FocusEvent) => void) {
            this.inputOccurrences.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.inputOccurrences.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.inputOccurrences.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.inputOccurrences.unBlur(listener);
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

        newInitialValue(): api.data.Value {
            throw new Error("Must be implemented by inheritor");
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            throw new Error("Must be implemented by inheritor");
        }

        onValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
            this.valueAddedListeners.push(listener);
        }

        unValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
            this.valueAddedListeners.filter((currentListener: (event: api.form.inputtype.ValueAddedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        notifyValueAdded(event: api.form.inputtype.ValueAddedEvent) {
            this.valueAddedListeners.forEach((listener: (event: api.form.inputtype.ValueAddedEvent)=>void) => {
                listener(event);
            });
        }

        onValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            this.valueChangedListeners.filter((currentListener: (event: api.form.inputtype.ValueChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValueChanged(event: api.form.inputtype.ValueChangedEvent) {
            this.valueChangedListeners.forEach((listener: (event: api.form.inputtype.ValueChangedEvent)=>void) => {
                listener(event);
            });
        }

        onValueRemoved(listener: (event: api.form.inputtype.ValueRemovedEvent) => void) {
            this.valueRemovedListeners.push(listener);
        }

        unValueRemoved(listener: (event: api.form.inputtype.ValueRemovedEvent) => void) {
            this.valueRemovedListeners.filter((currentListener: (event: api.form.inputtype.ValueRemovedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        notifyValueRemoved(event: api.form.inputtype.ValueRemovedEvent) {
            this.valueRemovedListeners.forEach((listener: (event: api.form.inputtype.ValueRemovedEvent)=>void) => {
                listener(event);
            });
        }

        /**
         * Note: Never fire ValueChangedEvent for null Value.
         */
        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            throw new Error("Must be implemented by inheritor");
        }

        giveFocus(): boolean {
            if (this.inputOccurrences) {
                return this.inputOccurrences.giveFocus();
            }
            return false;
        }

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Adapter for InputTypeView method, to be implemented on demand in inheritors
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Adapter for InputTypeView method, to be implemented on demand in inheritors
        }
    }
}

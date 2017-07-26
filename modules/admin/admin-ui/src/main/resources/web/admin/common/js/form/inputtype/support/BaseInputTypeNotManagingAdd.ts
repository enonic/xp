module api.form.inputtype.support {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import InputTypeView = api.form.inputtype.InputTypeView;
    import i18n = api.util.i18n;

    export class BaseInputTypeNotManagingAdd<RAW_VALUE_TYPE> extends api.dom.DivEl implements InputTypeView<RAW_VALUE_TYPE> {

        private context: api.form.inputtype.InputTypeViewContext;

        protected input: api.form.Input;

        protected propertyArray: PropertyArray;

        protected inputOccurrences: InputOccurrences;

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        private inputValueChangedListeners: {(occurrence: api.dom.Element, value: api.data.Value): void}[] = [];

        protected previousValidationRecording: api.form.inputtype.InputValidationRecording;

        /**
         * The index of child Data being dragged.
         */
        private draggingIndex: number;

        protected ignorePropertyChange: boolean;

        public static debug: boolean = false;

        constructor(context: api.form.inputtype.InputTypeViewContext, className?: string) {
            super('input-type-view' + ( className ? ' ' + className : ''));
            api.util.assertNotNull(context, 'context cannot be null');
            this.context = context;

            wemjq(this.getHTMLElement()).sortable({
                axis: 'y',
                containment: 'parent',
                handle: '.drag-control',
                tolerance: 'pointer',
                start: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStart(event, ui),
                stop: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStop(event, ui),
                update: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDUpdate(event, ui)
            });
        }

        handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {

            let draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
            this.draggingIndex = draggedElement.getSiblingIndex();

            ui.placeholder.html('Drop form item set here');
        }

        handleDnDStop(event: Event, ui: JQueryUI.SortableUIParams): void {
            //override in child classes if needed
        }

        handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            if (this.draggingIndex >= 0) {
                let draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
                let draggedToIndex = draggedElement.getSiblingIndex();
                this.inputOccurrences.moveOccurrence(this.draggingIndex, draggedToIndex);
            }

            this.draggingIndex = -1;
        }

        availableSizeChanged() {
            // must be implemented by children
        }

        public getContext(): api.form.inputtype.InputTypeViewContext {
            return this.context;
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

        onOccurrenceRendered(listener: (event: api.form.OccurrenceRenderedEvent) => void) {
            this.inputOccurrences.onOccurrenceRendered(listener);
        }

        onOccurrenceRemoved(listener: (event: api.form.OccurrenceRemovedEvent)=>void) {
            this.inputOccurrences.onOccurrenceRemoved(listener);
        }

        unOccurrenceAdded(listener: (event: api.form.OccurrenceAddedEvent)=>void) {
            this.inputOccurrences.unOccurrenceAdded(listener);
        }

        unOccurrenceRendered(listener: (event: api.form.OccurrenceRenderedEvent) => void) {
            this.inputOccurrences.onOccurrenceRendered(listener);
        }

        unOccurrenceRemoved(listener: (event: api.form.OccurrenceRemovedEvent)=>void) {
            this.inputOccurrences.unOccurrenceRemoved(listener);
        }

        onOccurrenceValueChanged(listener: (occurrence: api.dom.Element, value: api.data.Value) => void) {
            this.inputValueChangedListeners.push(listener);
        }

        unOccurrenceValueChanged(listener: (occurrence: api.dom.Element, value: api.data.Value) => void) {
            this.inputValueChangedListeners = this.inputValueChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        protected notifyOccurrenceValueChanged(occurrence: api.dom.Element, value: api.data.Value) {
            this.inputValueChangedListeners.forEach((listener: (occurrence: api.dom.Element, value: api.data.Value)=>void) => {
                listener(occurrence, value);
            });
        }

        onValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            throw new Error('User onOccurrenceValueChanged instead');
        }

        unValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            throw new Error('User onOccurrenceValueChanged instead');
        }

        onValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.filter((currentListener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                return listener === currentListener;
            });
        }

        protected notifyValidityChanged(event: api.form.inputtype.InputValidityChangedEvent) {
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

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {

            this.input = input;
            this.propertyArray = propertyArray;
            this.inputOccurrences = InputOccurrences.create().
                setBaseInputTypeView(this).
                setInput(this.input).
                setPropertyArray(propertyArray).
                build();

            this.onAdded((event: api.dom.ElementAddedEvent) => {
                this.onOccurrenceAdded(() => {
                    wemjq(this.getHTMLElement()).sortable('refresh');
                });
            });

            return this.inputOccurrences.layout().then(() => {
                wemjq(this.getHTMLElement()).sortable('refresh');
            });
        }

        update(propertyArray: api.data.PropertyArray, unchangedOnly?: boolean): Q.Promise<void> {
            this.propertyArray = propertyArray;

            return this.inputOccurrences.update(propertyArray, unchangedOnly);
        }

        reset() {
            this.inputOccurrences.reset();
        }

        hasValidUserInput(): boolean {
            return this.inputOccurrences.hasValidUserInput();
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element): boolean {
            throw new Error('Must be implemented by inheritor');
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

        displayValidationErrors(value: boolean) {
            // must be implemented by children
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            let recording = this.validateOccurrences();

            if (!this.hasValidUserInput()) {
                recording.setAdditionalValidationRecord(api.form.AdditionalValidationRecord.create().
                    setOverwriteDefault(true).
                    setMessage(i18n('notify.field.wrong.value')).
                    build());
            } else {
                this.additionalValidate(recording);
            }

            if (!silent && recording.validityChanged(this.previousValidationRecording)) {
                this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
            }

            this.previousValidationRecording = recording;
            return recording.clone();
        }

        protected additionalValidate(recording: api.form.inputtype.InputValidationRecording) {
            //Do nothing
        }

        protected validateOccurrences(): api.form.inputtype.InputValidationRecording {
            let recording = new api.form.inputtype.InputValidationRecording();
            let numberOfValids = 0;
            this.inputOccurrences.getOccurrenceViews().forEach((occurrenceView: InputOccurrenceView) => {

                let valueFromPropertyArray = this.propertyArray.getValue(occurrenceView.getIndex());
                if (valueFromPropertyArray) {
                    if (!this.valueBreaksRequiredContract(valueFromPropertyArray) && this.hasValidUserInput()) {
                        numberOfValids++;
                    }
                }
            });

            if (numberOfValids < this.input.getOccurrences().getMinimum()) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (this.input.getOccurrences().maximumBreached(numberOfValids)) {
                recording.setBreaksMaximumOccurrences(true);
            }

            return recording;
        }

        protected getPropertyValue(property: Property): string {
            return property.hasNonNullValue() ? property.getString() : '';
        }

        notifyRequiredContractBroken(state: boolean, index: number) {

            this.validate(false);
        }

        getInput(): api.form.Input {
            return this.input;
        }

        valueBreaksRequiredContract(value: Value): boolean {
            throw new Error('Must be implemented by inheritor: ' + api.ClassHelper.getClassName(this));
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            throw new Error('Must be implemented by inheritor: ' + api.ClassHelper.getClassName(this));
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: Property, unchangedOnly?: boolean) {
            throw new Error('Must be implemented by inheritor: ' + api.ClassHelper.getClassName(this));
        }

        resetInputOccurrenceElement(occurrence: api.dom.Element) {
            throw new Error('Must be implemented by inheritor: ' + api.ClassHelper.getClassName(this));
        }

        getValueType(): ValueType {
            throw new Error('Must be implemented by inheritor: ' + api.ClassHelper.getClassName(this));
        }

        newInitialValue(): Value {
            return this.input ? this.input.getDefaultValue() : null;
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

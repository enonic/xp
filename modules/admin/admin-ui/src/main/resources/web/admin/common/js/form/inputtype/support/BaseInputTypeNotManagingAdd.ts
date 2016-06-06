module api.form.inputtype.support {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;

    export class BaseInputTypeNotManagingAdd<RAW_VALUE_TYPE> extends api.dom.DivEl implements api.form.inputtype.InputTypeView<RAW_VALUE_TYPE> {

        private context: api.form.inputtype.InputTypeViewContext;

        private input: api.form.Input;

        private propertyArray: PropertyArray;

        private inputOccurrences: InputOccurrences;

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        private inputValueChangedListeners: {(occurrence: api.dom.Element, value: api.data.Value): void}[] = [];

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        /**
         * The index of child Data being dragged.
         */
        private draggingIndex: number;

        protected ignorePropertyChange: boolean;

        public static debug: boolean = false;

        constructor(context: api.form.inputtype.InputTypeViewContext, className?: string) {
            super("input-type-view" + ( className ? " " + className : ""));
            api.util.assertNotNull(context, "context cannot be null");
            this.context = context;

            wemjq(this.getHTMLElement()).sortable({
                axis: "y",
                containment: 'parent',
                handle: '.drag-control',
                tolerance: 'pointer',
                start: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStart(event, ui),
                stop: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStop(event, ui),
                update: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDUpdate(event, ui)
            });
        }

        handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {

            var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
            this.draggingIndex = draggedElement.getSiblingIndex();

            ui.placeholder.html("Drop form item set here");
        }

        handleDnDStop(event: Event, ui: JQueryUI.SortableUIParams): void {
            //override in child classes if needed
        }

        handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            if (this.draggingIndex >= 0) {
                var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
                var draggedToIndex = draggedElement.getSiblingIndex();
                this.inputOccurrences.moveOccurrence(this.draggingIndex, draggedToIndex);
            }

            this.draggingIndex = -1;
        }

        availableSizeChanged() {

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
            })
        }

        protected notifyOccurrenceValueChanged(occurrence: api.dom.Element, value: api.data.Value) {
            this.inputValueChangedListeners.forEach((listener: (occurrence: api.dom.Element, value: api.data.Value)=>void) => {
                listener(occurrence, value);
            });
        }

        onValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            throw new Error("User onOccurrenceValueChanged instead");
        }

        unValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            throw new Error("User onOccurrenceValueChanged instead");
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
                    wemjq(this.getHTMLElement()).sortable("refresh");
                });
            });

            return this.inputOccurrences.layout().then(() => {
                wemjq(this.getHTMLElement()).sortable("refresh");
            });
        }

        update(propertyArray: api.data.PropertyArray, unchangedOnly?: boolean): Q.Promise<void> {
            this.propertyArray = propertyArray;

            return this.inputOccurrences.update(propertyArray, unchangedOnly);
        }

        hasValidUserInput(): boolean {
            return this.inputOccurrences.hasValidUserInput();
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element): boolean {
            throw new Error("Must be implemented by inheritor");
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

        /**
         * Override when needed.
         */
        displayValidationErrors(value: boolean) {

        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            var recording = new api.form.inputtype.InputValidationRecording();
            var numberOfValids = 0;
            this.inputOccurrences.getOccurrenceViews().forEach((occurrenceView: InputOccurrenceView) => {

                var valueFromPropertyArray = this.propertyArray.getValue(occurrenceView.getIndex());
                if (valueFromPropertyArray) {
                    var breaksRequiredContract = this.valueBreaksRequiredContract(valueFromPropertyArray);
                    if (!breaksRequiredContract) {
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

            var additionalValidation: api.form.AdditionalValidationRecord = this.getSpecialValidation();
            recording.setAdditionalValidationRecord(additionalValidation);

            if (!silent && recording.validityChanged(this.previousValidationRecording)) {
                this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        getSpecialValidation(): api.form.AdditionalValidationRecord {
            var result = api.form.AdditionalValidationRecord.create().setOverwriteDefault(false).build();

            this.inputOccurrences.getOccurrenceViews().forEach((occurrenceView: api.form.inputtype.support.InputOccurrenceView) => {
                var picker;

                if (api.ObjectHelper.iFrameSafeInstanceOf(occurrenceView.getInputElement(), api.ui.time.DatePicker)) {
                    picker = <api.ui.time.DatePicker>(occurrenceView.getInputElement());
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(occurrenceView.getInputElement(), api.ui.time.DateTimePicker)) {
                    picker = <api.ui.time.DateTimePicker>(occurrenceView.getInputElement())
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(occurrenceView.getInputElement(), api.ui.time.TimePicker)) {
                    picker = <api.ui.time.TimePicker>(occurrenceView.getInputElement());
                }

                if (picker && !picker.isValid()) {
                    result = api.form.AdditionalValidationRecord.create().
                        setOverwriteDefault(true).
                        setMessage("Incorrect value entered").
                        build();
                    return;
                }
            });

            return result;
        }

        protected getPropertyValue(property: Property): string {
            return property.hasNonNullValue() ? property.getString() : "";
        }

        notifyRequiredContractBroken(state: boolean, index: number) {

            this.validate(false);
        }

        getInput(): api.form.Input {
            return this.input;
        }

        valueBreaksRequiredContract(value: Value): boolean {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }


        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: Property, unchangedOnly?: boolean) {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        getValueType(): ValueType {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
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

module api.form.inputtype.support {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;

    export class BaseInputTypeNotManagingAdd<CONFIG,RAW_VALUE_TYPE> extends api.dom.DivEl implements api.form.inputtype.InputTypeViewNotManagingAdd<RAW_VALUE_TYPE> {

        private context: api.form.inputtype.InputTypeViewContext<CONFIG>;

        private input: api.form.Input;

        private propertyArray: PropertyArray;

        private inputOccurrences: InputOccurrences;

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        /**
         * The index of child Data being dragged.
         */
        private draggingIndex: number;

        constructor(context: api.form.inputtype.InputTypeViewContext<CONFIG>, className?: string) {
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

        public getContext(): api.form.inputtype.InputTypeViewContext<CONFIG> {
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
            if (!silent) {

                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
                }
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        getSpecialValidation(): api.form.AdditionalValidationRecord {
            var result = api.form.AdditionalValidationRecord.create().setOverwriteDefault(false).build();

            this.inputOccurrences.getOccurrenceViews().forEach((occurrenceView: api.form.inputtype.support.InputOccurrenceView) => {
                var picker;

                debugger;

                if (api.ObjectHelper.iFrameSafeInstanceOf(occurrenceView.getInputElement(), api.ui.time.DatePicker)) {
                    picker = <api.ui.time.DatePicker>(occurrenceView.getInputElement());
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(occurrenceView.getInputElement(), api.ui.time.DateTimePicker)) {
                    picker = <api.ui.time.DateTimePicker>(occurrenceView.getInputElement())
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(occurrenceView.getInputElement(), api.ui.time.LocalTime)) {
                    picker = (<api.ui.time.LocalTime>(occurrenceView.getInputElement())).getTimePicker();
                }

                if (picker && !picker.hasValidUserInput()) {
                    result = api.form.AdditionalValidationRecord.create().
                        setOverwriteDefault(true).
                        setMessage("Incorrect value entered").
                        build();
                    return;
                }
            });

            return result;
        }

        protected onValueChanged(property: Property, value: Object, type: ValueType) {
            var newValue = new Value(value, type);
            property.setValue(newValue);
            this.validate(false);
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

        getValueType(): ValueType {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        newInitialValue(): Value {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
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

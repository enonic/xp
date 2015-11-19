module api.form.inputtype.support {

    import PropertyPath = api.data.PropertyPath;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class BaseInputTypeManagingAdd<RAW_VALUE_TYPE> extends api.dom.DivEl implements api.form.inputtype.InputTypeView<RAW_VALUE_TYPE> {

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        private input: api.form.Input;

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        private layoutInProgress: boolean;

        private propertyArray: PropertyArray;

        constructor(className: string) {
            super("input-type-view" + (className ? " " + className : ""));
        }

        availableSizeChanged() {

        }

        getElement(): api.dom.Element {
            return this;
        }

        isManagingAdd(): boolean {
            return true;
        }

        getValueType(): ValueType {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        /**
         * Must be overridden by inheritors.
         */
        newInitialValue(): Value {
            throw new Error("Must be overridden by inheritor: " + api.ClassHelper.getClassName(this));
        }

        /**
         * Must be resolved by inheritors.
         */
        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {
            this.input = input;
            this.layoutInProgress = true;
            this.propertyArray = propertyArray;

            return wemQ<void>(null);
        }

        /**
         * Must be resolved by inheritors.
         */
        update(propertyArray: PropertyArray, unchangedOnly?: boolean): wemQ.Promise<void> {
            this.propertyArray = propertyArray;

            return wemQ<void>(null);
        }

        hasValidUserInput(): boolean {
            return true;
        }

        /**
         * Override when needed.
         */
        displayValidationErrors(value: boolean) {

        }

        validate(silent: boolean = true, rec: api.form.inputtype.InputValidationRecording = null): api.form.inputtype.InputValidationRecording {

            var recording = rec || new api.form.inputtype.InputValidationRecording();

            if (this.layoutInProgress) {
                this.previousValidationRecording = recording;
                return recording;
            }

            var numberOfValids = this.getNumberOfValids();

            if (this.input.getOccurrences().minimumBreached(numberOfValids)) {
                recording.setBreaksMinimumOccurrences(true);
            }

            if (this.input.getOccurrences().maximumBreached(numberOfValids)) {
                recording.setBreaksMaximumOccurrences(true);
            }

            if (!silent && recording.validityChanged(this.previousValidationRecording)) {
                this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        onValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.filter((currentListener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        notifyValidityChanged(event: api.form.inputtype.InputValidityChangedEvent) {

            this.inputValidityChangedListeners.forEach((listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                listener(event);
            });
        }

        /**
         * Must be overridden by inheritors.
         */
        giveFocus(): boolean {
            throw new Error("Must be overridden by inheritor: " + api.ClassHelper.getClassName(this));
        }

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseInputTypeView
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseInputTypeView
        }

        protected getInput(): api.form.Input {
            return this.input;
        }

        protected getNumberOfValids(): number {
            throw new Error("Must be overridden by inheritor: " + api.ClassHelper.getClassName(this));
        }

        protected isLayoutInProgress(): boolean {
            return this.layoutInProgress;
        }

        protected setLayoutInProgress(layoutInProgress: boolean) {
            this.layoutInProgress = layoutInProgress;
        }

        protected getPropertyArray(): PropertyArray {
            return this.propertyArray;
        }
    }
}

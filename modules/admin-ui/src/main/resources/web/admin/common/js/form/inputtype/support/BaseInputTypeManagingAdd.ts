module api.form.inputtype.support {

    import PropertyPath = api.data.PropertyPath;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class BaseInputTypeManagingAdd<RAW_VALUE_TYPE> extends api.dom.DivEl implements api.form.inputtype.InputTypeView<RAW_VALUE_TYPE> {

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

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
         * Must be overridden by inheritors.
         */
        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {

            throw new Error("Must be overridden by inheritor: " + api.ClassHelper.getClassName(this));
        }

        hasValidUserInput(): boolean {
            return true;
        }

        /**
         * Override when needed.
         */
        displayValidationErrors(value: boolean) {

        }

        /**
         * Must be overridden by inheritors.
         */
        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            throw new Error("Must be overridden by inheritor: " + api.ClassHelper.getClassName(this));
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


    }
}

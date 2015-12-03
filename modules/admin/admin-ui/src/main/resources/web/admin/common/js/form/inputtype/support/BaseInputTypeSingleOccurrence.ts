module api.form.inputtype.support {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;

    export class BaseInputTypeSingleOccurrence<RAW_VALUE_TYPE> extends api.dom.DivEl implements api.form.inputtype.InputTypeView<RAW_VALUE_TYPE> {

        private context: api.form.inputtype.InputTypeViewContext;

        protected input: api.form.Input;

        protected ignorePropertyChange: boolean;

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        constructor(ctx: api.form.inputtype.InputTypeViewContext, className?: string) {
            super("input-type-view" + ( className ? " " + className : ""));
            api.util.assertNotNull(ctx, "CONTEXT cannot be null");
            this.context = ctx;
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
            return true;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {
            this.layoutProperty(input, propertyArray.get(0));
            return wemQ<void>(null);
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {

            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        update(propertyArray: PropertyArray, unchangedOnly?: boolean): wemQ.Promise<void> {
            return this.updateProperty(propertyArray.get(0), unchangedOnly);
        }

        updateProperty(property: Property, unchangedOnly?: boolean): wemQ.Promise<void> {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        getValueType(): ValueType {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        newInitialValue(): Value {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        /**
         * Override when needed.
         */
        displayValidationErrors(value: boolean) {

        }

        hasValidUserInput(): boolean {
            return true;
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        protected onValueChanged(property: Property, value: Object, type: ValueType) {
            this.ignorePropertyChange = true;
            var newValue = new Value(value, type);
            property.setValue(newValue);
            this.validate(false);
            this.ignorePropertyChange = false;
        }

        protected notifyValidityChanged(event: api.form.inputtype.InputValidityChangedEvent) {
            this.inputValidityChangedListeners.forEach((listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                listener(event);
            });
        }

        onValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.filter((currentListener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        onFocus(listener: (event: FocusEvent) => void) {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        unFocus(listener: (event: FocusEvent) => void) {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        onBlur(listener: (event: FocusEvent) => void) {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        unBlur(listener: (event: FocusEvent) => void) {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Adapter for InputTypeView method, to be implemented on demand in inheritors
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Adapter for InputTypeView method, to be implemented on demand in inheritors
        }
    }
}

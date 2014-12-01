module api.form.inputtype.support {

    import PropertyPath = api.data2.PropertyPath;
    import Property = api.data2.Property;
    import PropertyArray = api.data2.PropertyArray;
    import Value = api.data2.Value;
    import ValueType = api.data2.ValueType;
    import ValueTypes = api.data2.ValueTypes;

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
        layout(input: api.form.Input, propertyArray: PropertyArray) {

            throw new Error("Must be overridden by inheritor: " + api.ClassHelper.getClassName(this));
        }


        getAttachments(): api.content.attachment.Attachment[] {
            return [];
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

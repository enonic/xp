module api.form.inputtype.support {

    import DataPath = api.data.DataPath;

    export class BaseInputTypeManagingAdd extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private valueAddedListeners: {(event: api.form.inputtype.ValueAddedEvent) : void}[] = [];

        private valueChangedListeners: {(event: api.form.inputtype.ValueChangedEvent) : void}[] = [];

        private valueRemovedListeners: {(event: api.form.inputtype.ValueRemovedEvent) : void}[] = [];

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        constructor(className: string) {
            super(className);
        }

        availableSizeChanged() {

        }

        getElement(): api.dom.Element {
            return this;
        }

        isManagingAdd(): boolean {
            return true;
        }

        getValueType(): api.data.type.ValueType {
            throw new Error("Must be implemented by inheritor: " + api.util.getClassName(this));
        }

        /**
         * Must be overridden by inheritors.
         */
        newInitialValue(): any {
            throw new Error("Must be overridden by inheritor: " + api.util.getClassName(this));
        }

        /**
         * Must be overridden by inheritors.
         */
        layout(input: api.form.Input, properties: api.data.Property[]) {

            throw new Error("Must be overridden by inheritor: " + api.util.getClassName(this));
        }

        /**
         * Must be overridden by inheritors.
         */
        getValues(): api.data.Value[] {
            throw new Error("Must be overridden by inheritor: " + api.util.getClassName(this));
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return [];
        }

        /**
         * Must be overridden by inheritors.
         */
        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            throw new Error("Must be overridden by inheritor: " + api.util.getClassName(this));
        }

        onValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
            this.valueAddedListeners.push(listener);
        }

        unValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
            this.valueAddedListeners.filter((currentListener: (event: api.form.inputtype.ValueAddedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        notifyValueAdded(value: api.data.Value) {
            var event = new api.form.inputtype.ValueAddedEvent(value);
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

        notifyValueChanged(event: api.form.inputtype.ValueChangedEvent) {
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

        notifyValueRemoved(index: number) {
            var event = new api.form.inputtype.ValueRemovedEvent(index);
            this.valueRemovedListeners.forEach((listener: (event: api.form.inputtype.ValueRemovedEvent)=>void) => {
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

        notifyValidityChanged(event: api.form.inputtype.InputValidityChangedEvent) {

            this.inputValidityChangedListeners.forEach((listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                listener(event);
            });
        }

        /**
         * Must be overridden by inheritors.
         */
        giveFocus(): boolean {
            throw new Error("Must be overridden by inheritor: " + api.util.getClassName(this));
        }

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseInputTypeView
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseInputTypeView
        }


    }
}

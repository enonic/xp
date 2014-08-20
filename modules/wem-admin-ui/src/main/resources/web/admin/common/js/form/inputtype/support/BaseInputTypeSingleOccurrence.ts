module api.form.inputtype.support {

    export class BaseInputTypeSingleOccurrence<CONFIG> extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private config: api.form.inputtype.InputTypeViewContext<CONFIG>;

        private input: api.form.Input;

        private valueChangedListeners: {(event: api.form.inputtype.ValueChangedEvent) : void}[] = [];

        constructor(config: api.form.inputtype.InputTypeViewContext<CONFIG>, className?: string) {
            super("input-type-view" + ( className ? " " + className : ""));
            api.util.assertNotNull(config, "config cannt be null");
            this.config = config;
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
            return true;
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            throw new Error("Must be implemented by inheritor");
        }

        newInitialValue(): api.data.Value {
            throw new Error("Must be implemented by inheritor");
        }

        getValues(): api.data.Value[] {

            throw new Error("Must be implemented by inheritor");
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return [];
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            throw new Error("Must be implemented by inheritor");
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

        onValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
        }

        unValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
        }

        onValueRemoved(listener: (event: api.form.inputtype.ValueRemovedEvent) => void) {
        }

        unValueRemoved(listener: (event: api.form.inputtype.ValueRemovedEvent) => void) {

        }

        onValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
        }

        unValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
        }

        onFocus(listener: (event: FocusEvent) => void) {
            throw new Error("Must be implemented by inheritor");
        }

        unFocus(listener: (event: FocusEvent) => void) {
            throw new Error("Must be implemented by inheritor");
        }

        onBlur(listener: (event: FocusEvent) => void) {
            throw new Error("Must be implemented by inheritor");
        }

        unBlur(listener: (event: FocusEvent) => void) {
            throw new Error("Must be implemented by inheritor");
        }

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Adapter for InputTypeView method, to be implemented on demand in inheritors
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Adapter for InputTypeView method, to be implemented on demand in inheritors
        }
    }
}

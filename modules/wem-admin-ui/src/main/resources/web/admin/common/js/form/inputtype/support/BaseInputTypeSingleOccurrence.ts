module api.form.inputtype.support {

    export class BaseInputTypeSingleOccurrence<CONTEXT,RAW_VALUE_TYPE> extends api.dom.DivEl implements api.form.inputtype.InputTypeView<RAW_VALUE_TYPE> {

        private context: api.form.inputtype.InputTypeViewContext<CONTEXT>;

        private input: api.form.Input;

        private valueChangedListeners: {(event: api.form.inputtype.ValueChangedEvent) : void}[] = [];

        constructor(CONTEXT: api.form.inputtype.InputTypeViewContext<CONTEXT>, className?: string) {
            super("input-type-view" + ( className ? " " + className : ""));
            api.util.assertNotNull(CONTEXT, "CONTEXT cannt be null");
            this.context = CONTEXT;
        }

        availableSizeChanged() {

        }

        public getContext(): api.form.inputtype.InputTypeViewContext<CONTEXT> {
            return this.context;
        }

        getElement(): api.dom.Element {
            return this;
        }

        isManagingAdd(): boolean {
            return true;
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {
            this.layoutProperty(input, properties[0])
        }

        layoutProperty(input: api.form.Input, property: api.data.Property) {

            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        getValueType(): api.data.type.ValueType {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        newInitialValue(): RAW_VALUE_TYPE {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        getValues(): api.data.Value[] {

            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return [];
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
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

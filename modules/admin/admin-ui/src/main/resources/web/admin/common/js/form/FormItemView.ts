module api.form {

    import PropertyPath = api.data.PropertyPath;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import PropertyTree = api.data.PropertyTree;
    import PropertySet = api.data.PropertySet;

    export interface FormItemViewConfig {

        className: string;

        context: FormContext;

        formItem: FormItem;

        parent: FormItemOccurrenceView;
    }

    export class FormItemView extends api.dom.DivEl {

        private context: FormContext;

        private formItem: FormItem;

        private parent: FormItemOccurrenceView;

        private editContentRequestListeners: {(content: api.content.ContentSummary): void}[] = [];

        private highlightOnValidityChanged: boolean;

        constructor(config: FormItemViewConfig) {
            super(config.className);
            api.util.assertNotNull(config.context, "context cannot be null");
            api.util.assertNotNull(config.formItem, "formItem cannot be null");
            this.context = config.context;
            this.formItem = config.formItem;
            this.parent = config.parent;
            this.highlightOnValidityChanged = false;
        }

        public setHighlightOnValidityChange(highlight: boolean) {
            this.highlightOnValidityChanged = highlight;
        }

        broadcastFormSizeChanged() {
            throw new Error("Must be implemented by inheritors");
        }

        layout(): wemQ.Promise<void> {
            throw new Error("Must be implemented by inheritors");
        }

        update(propertyArray: PropertySet, unchangedOnly?: boolean): wemQ.Promise<void> {
            throw new Error("Must be implemented by inheritors");
        }

        reset() {
            throw new Error("Must be implemented by inheritors");
        }

        getContext(): FormContext {
            return this.context;
        }

        getFormItem(): FormItem {
            return this.formItem;
        }

        getParent(): FormItemOccurrenceView {
            return this.parent;
        }

        public displayValidationErrors(value: boolean) {
            throw new Error("Must be implemented by inheritor");
        }

        hasValidUserInput(): boolean {
            throw new Error("Must be implemented by inheritor");
        }

        validate(silent: boolean = true): ValidationRecording {

            // Default method to avoid having to implement method in Layout-s.
            return new ValidationRecording();
        }

        giveFocus(): boolean {
            return false;
        }

        highlightOnValidityChange(): boolean {
            return this.highlightOnValidityChanged;
        }

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            this.editContentRequestListeners.push(listener);
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            this.editContentRequestListeners = this.editContentRequestListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifyEditContentRequested(content: api.content.ContentSummary) {
            this.editContentRequestListeners.forEach((listener) => {
                listener(content);
            })
        }

        onValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            //Should be implemented in child classes
        }

        unValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            //Should be implemented in child classes
        }

        toggleHelpText(show?: boolean) {
            // TO BE IMPLEMENTED BY INHERITORS
        }

        hasHelpText(): boolean {
            return false;
        }
    }
}
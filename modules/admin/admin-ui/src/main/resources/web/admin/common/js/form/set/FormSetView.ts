module api.form {

    import PropertySet = api.data.PropertySet;
    import PropertyArray = api.data.PropertyArray;

    export class FormSetView<V extends FormSetOccurrenceView> extends FormItemView {

        protected parentDataSet: PropertySet;

        protected occurrenceViewsContainer: api.dom.DivEl;

        protected bottomButtonRow: api.dom.DivEl;

        protected addButton: api.ui.button.Button;

        protected collapseButton: api.dom.AEl;

        protected validityChangedListeners: {(event: RecordingValidityChangedEvent) : void}[] = [];

        protected previousValidationRecording: ValidationRecording;

        protected formItemOccurrences: FormSetOccurrences<V>;

        /**
         * The index of child Data being dragged.
         */
        protected draggingIndex: number;

        constructor(config: FormItemViewConfig) {
            super(config);
        }

        reset() {
            this.formItemOccurrences.reset();
        }

        broadcastFormSizeChanged() {
            this.formItemOccurrences.getOccurrenceViews().forEach((occurrenceView: FormSetOccurrenceView) => {
                occurrenceView.getFormItemViews().forEach((formItemView: FormItemView) => {
                    formItemView.broadcastFormSizeChanged();
                });
            });
        }

        refresh() {
            this.collapseButton.setVisible(this.formItemOccurrences.getOccurrences().length > 0);
            this.addButton.setVisible(!this.formItemOccurrences.maximumOccurrencesReached());
        }

        update(propertySet: api.data.PropertySet, unchangedOnly?: boolean): Q.Promise<void> {
            this.parentDataSet = propertySet;
            var propertyArray = this.getPropertyArray(propertySet);
            return this.formItemOccurrences.update(propertyArray, unchangedOnly);
        }

        public displayValidationErrors(value: boolean) {
            this.formItemOccurrences.getOccurrenceViews().forEach((view: FormSetOccurrenceView) => {
                view.displayValidationErrors(value);
            });
        }

        public setHighlightOnValidityChange(highlight: boolean) {
            this.formItemOccurrences.getOccurrenceViews().forEach((view: FormSetOccurrenceView) => {
                view.setHighlightOnValidityChange(highlight);
            });
        }

        protected getPropertyArray(parentPropertySet: PropertySet): PropertyArray {
            throw new Error("Must be implemented by inheritor");
        }

        hasValidUserInput(): boolean {

            var result = true;
            this.formItemOccurrences.getOccurrenceViews().forEach((formItemOccurrenceView: FormItemOccurrenceView) => {
                if (!formItemOccurrenceView.hasValidUserInput()) {
                    result = false;
                }
            });

            return result;
        }

        onValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            this.validityChangedListeners.filter((currentListener: (event: RecordingValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        protected notifyValidityChanged(event: RecordingValidityChangedEvent) {
            this.validityChangedListeners.forEach((listener: (event: RecordingValidityChangedEvent)=>void) => {
                listener(event);
            });
        }

        protected renderValidationErrors(recording: ValidationRecording) {
            if (recording.isValid()) {
                this.removeClass("invalid");
                this.addClass("valid");
            }
            else {
                this.removeClass("valid");
                this.addClass("invalid");
            }
        }

        giveFocus(): boolean {

            var focusGiven = false;
            if (this.formItemOccurrences.getOccurrenceViews().length > 0) {
                var views: FormItemOccurrenceView[] = this.formItemOccurrences.getOccurrenceViews();
                for (var i = 0; i < views.length; i++) {
                    if (views[i].giveFocus()) {
                        focusGiven = true;
                        break;
                    }
                }
            }
            return focusGiven;
        }

        reset() {
            this.formItemOccurrences.reset();
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.formItemOccurrences.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.formItemOccurrences.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.formItemOccurrences.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.formItemOccurrences.unBlur(listener);
        }

    }

}
module api.form {

    import PropertySet = api.data.PropertySet;
    import PropertyArray = api.data.PropertyArray;

    export class FormSetOccurrenceView extends FormItemOccurrenceView {

        protected formItemViews: FormItemView[] = [];

        protected validityChangedListeners: {(event: RecordingValidityChangedEvent) : void}[] = [];

        protected removeButton: api.dom.AEl;

        protected label: FormOccurrenceDraggableLabel;

        protected currentValidationState: ValidationRecording;

        protected formItemLayer: FormItemLayer;

        protected propertySet: PropertySet;

        protected formItemSetOccurrencesContainer: api.dom.DivEl;

        constructor(className, formItemOccurrence: FormItemOccurrence<FormItemOccurrenceView>) {
            super(className, formItemOccurrence);
        }

        update(propertyArray: PropertyArray, unchangedOnly?: boolean): wemQ.Promise<void> {
            var set = propertyArray.getSet(this.formItemOccurrence.getIndex());
            if (!set) {
                set = propertyArray.addSet();
            }
            this.propertySet = set;
            return this.formItemLayer.update(this.propertySet, unchangedOnly);
        }

        hasValidUserInput(): boolean {

            var result = true;
            this.formItemViews.forEach((formItemView: FormItemView) => {
                if (!formItemView.hasValidUserInput()) {
                    result = false;
                }
            });
            return result;
        }

        showContainer(show: boolean) {
            if (show) {
                this.formItemSetOccurrencesContainer.show();
            } else {
                this.formItemSetOccurrencesContainer.hide();
            }
        }

        refresh() {

            if (!this.formItemOccurrence.oneAndOnly()) {
                this.label.addClass("drag-control");
            } else {
                this.label.removeClass("drag-control");
            }

            this.removeButton.setVisible(this.formItemOccurrence.isRemoveButtonRequired());
        }

        protected resolveValidationRecordingPath(): ValidationRecordingPath {
            return new ValidationRecordingPath(this.getDataPath(), null);
        }

        getValidationRecording(): ValidationRecording {
            return this.currentValidationState;
        }

        getFormItemViews(): FormItemView[] {
            return this.formItemViews;
        }

        giveFocus() {
            var focusGiven = false;
            this.getFormItemViews().forEach((formItemView: FormItemView) => {
                if (!focusGiven && formItemView.giveFocus()) {
                    focusGiven = true;
                }
            });
            return focusGiven;
        }

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.onEditContentRequest(listener);
            });
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.unEditContentRequest(listener);
            });
        }

        public displayValidationErrors(value: boolean) {
            this.formItemViews.forEach((view: FormItemView) => {
                view.displayValidationErrors(value);
            });
        }

        public setHighlightOnValidityChange(highlight: boolean) {
            this.formItemViews.forEach((view: FormItemView) => {
                view.setHighlightOnValidityChange(highlight);
            });
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

        onFocus(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.onFocus(listener);
            });
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.unFocus(listener);
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.onBlur(listener);
            });
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.unBlur(listener);
            });
        }
    }

}
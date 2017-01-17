module api.form {

    import PropertySet = api.data.PropertySet;
    import PropertyArray = api.data.PropertyArray;

    export class FormSetOccurrenceView extends FormItemOccurrenceView {

        protected formItemViews: FormItemView[] = [];

        protected validityChangedListeners: {(event: RecordingValidityChangedEvent): void}[] = [];

        protected removeButton: api.dom.AEl;

        protected label: FormOccurrenceDraggableLabel;

        protected currentValidationState: ValidationRecording;

        protected formItemLayer: FormItemLayer;

        protected propertySet: PropertySet;

        protected formSetOccurrencesContainer: api.dom.DivEl;

        protected occurrenceContainerClassName: string;

        constructor(className: string, formItemOccurrence: FormItemOccurrence<FormItemOccurrenceView>) {
            super(className, formItemOccurrence);
        }

        public layout(validate: boolean = true): wemQ.Promise<void> {

            let deferred = wemQ.defer<void>();

            this.removeChildren();

            this.removeButton = new api.dom.AEl('remove-button');
            this.appendChild(this.removeButton);
            this.removeButton.onClicked((event: MouseEvent) => {
                this.notifyRemoveButtonClicked();
                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.label = new FormOccurrenceDraggableLabel(this.getFormSet().getLabel(), this.getFormSet().getOccurrences());
            this.appendChild(this.label);

            if (this.getFormSet().getHelpText()) {
                this.helpText = new HelpTextContainer(this.getFormSet().getHelpText());

                this.helpText.onHelpTextToggled((show) => {
                    this.formItemLayer.toggleHelpText(show);
                });

                this.label.appendChild(this.helpText.getToggler());
                this.appendChild(this.helpText.getHelpText());

                this.toggleHelpText(this.getFormSet().isHelpTextOn());
            }

            this.initValidationMessageBlock();

            this.formSetOccurrencesContainer = new api.dom.DivEl(this.occurrenceContainerClassName);
            this.appendChild(this.formSetOccurrencesContainer);

            let layoutPromise: wemQ.Promise<FormItemView[]> = this.formItemLayer.setFormItems(this.getFormItems()).setParentElement(
                this.formSetOccurrencesContainer).setParent(this).layout(this.propertySet, validate);

            layoutPromise.then((formItemViews: FormItemView[]) => {

                this.formItemViews = formItemViews;
                if (validate) {
                    this.validate(true);
                }

                this.subscribeOnItemEvents();

                this.refresh();
                deferred.resolve(null);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

            return deferred.promise;
        }

        protected initValidationMessageBlock() {
            // must be implemented by children
        }

        getDataPath(): api.data.PropertyPath {
            return this.propertySet.getProperty().getPath();
        }

        validate(silent: boolean = true): ValidationRecording {

            let allRecordings = new ValidationRecording();

            this.formItemViews.forEach((formItemView: FormItemView) => {
                let currRecording = formItemView.validate(silent);
                allRecordings.flatten(currRecording);
            });

            this.extraValidation(allRecordings);

            if (!silent) {
                if (allRecordings.validityChanged(this.currentValidationState)) {
                    this.notifyValidityChanged(new RecordingValidityChangedEvent(allRecordings, this.resolveValidationRecordingPath()));
                }
            }
            this.currentValidationState = allRecordings;
            return allRecordings;
        }

        protected extraValidation(validationRecording: ValidationRecording) {
            // must be implemented by children
        }

        protected subscribeOnItemEvents() {
            throw new Error('Must be implemented by inheritor');
        }

        protected getFormSet(): FormSet {
            throw new Error('Must be implemented by inheritor');
        }

        protected getFormItems(): FormItem[] {
            throw new Error('Must be implemented by inheritor');
        }

        toggleHelpText(show?: boolean): any {
            this.formItemLayer.toggleHelpText(show);
            return super.toggleHelpText(show);
        }

        update(propertyArray: PropertyArray, unchangedOnly?: boolean): wemQ.Promise<void> {
            let set = propertyArray.getSet(this.formItemOccurrence.getIndex());
            if (!set) {
                set = propertyArray.addSet();
            }
            this.ensureSelectionArrayExists(set);
            this.propertySet = set;
            return this.formItemLayer.update(this.propertySet, unchangedOnly);
        }

        hasValidUserInput(): boolean {

            let result = true;
            this.formItemViews.forEach((formItemView: FormItemView) => {
                if (!formItemView.hasValidUserInput()) {
                    result = false;
                }
            });
            return result;
        }

        protected ensureSelectionArrayExists(propertyArraySet: PropertySet) {
            // override if needed to add default selection to property set
        }

        showContainer(show: boolean) {
            if (show) {
                this.formSetOccurrencesContainer.show();
            } else {
                this.formSetOccurrencesContainer.hide();
            }
        }

        refresh() {

            if (!this.formItemOccurrence.oneAndOnly()) {
                this.label.addClass('drag-control');
            } else {
                this.label.removeClass('drag-control');
            }

            this.removeButton.setVisible(this.formItemOccurrence.isRemoveButtonRequired());
        }

        public reset() {
            return this.formItemLayer.reset();
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
            let focusGiven = false;
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

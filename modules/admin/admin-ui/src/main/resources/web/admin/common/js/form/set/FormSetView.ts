module api.form {

    import PropertySet = api.data.PropertySet;
    import PropertyArray = api.data.PropertyArray;
    import DragHelper = api.ui.DragHelper;

    export class FormSetView<V extends FormSetOccurrenceView> extends FormItemView {

        protected parentDataSet: PropertySet;

        protected occurrenceViewsContainer: api.dom.DivEl;

        protected bottomButtonRow: api.dom.DivEl;

        protected addButton: api.ui.button.Button;

        protected collapseButton: api.dom.AEl;

        protected validityChangedListeners: {(event: RecordingValidityChangedEvent): void}[] = [];

        protected previousValidationRecording: ValidationRecording;

        protected formItemOccurrences: FormSetOccurrences<V>;

        protected classPrefix = "";

        protected helpText: string;

        protected formSet: FormSet;

        /**
         * The index of child Data being dragged.
         */
        protected draggingIndex: number;

        constructor(config: FormItemViewConfig) {
            super(config);
        }

        public layout(validate: boolean = true): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.occurrenceViewsContainer = new api.dom.DivEl("occurrence-views-container");

            wemjq(this.occurrenceViewsContainer.getHTMLElement()).sortable({
                revert: false,
                containment: this.getHTMLElement(),
                cursor: 'move',
                cursorAt: {left: 14, top: 14},
                distance: 20,
                tolerance: 'pointer',
                handle: '.drag-control',
                placeholder: this.classPrefix + '-drop-target-placeholder',
                helper: (event, ui) => api.ui.DragHelper.get().getHTMLElement(),
                start: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStart(event, ui),
                update: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDUpdate(event, ui)
            });

            this.appendChild(this.occurrenceViewsContainer);

            this.initOccurrences().layout(validate).then(() => {

                this.subscribeFormSetOccurrencesOnEvents();

                this.bottomButtonRow = new api.dom.DivEl("bottom-button-row");
                this.appendChild(this.bottomButtonRow);

                this.bottomButtonRow.appendChild(this.addButton = this.makeAddButton());
                this.bottomButtonRow.appendChild(this.collapseButton = this.makeCollapseButton());

                this.refresh();

                if (validate) {
                    this.validate(true);
                }

                deferred.resolve(null);
            });

            return deferred.promise;
        }

        private subscribeFormSetOccurrencesOnEvents() {

            this.formItemOccurrences.onOccurrenceRendered((event: OccurrenceRenderedEvent) => {
                this.validate(false, event.validateViewOnRender() ? null : event.getOccurrenceView());
            });

            this.formItemOccurrences.onOccurrenceAdded((event: OccurrenceAddedEvent) => {
                this.refresh();
                wemjq(this.occurrenceViewsContainer.getHTMLElement()).sortable("refresh");

                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getOccurrenceView(), FormSetOccurrenceView)) {
                    var addedFormSetOccurrenceView = <V>event.getOccurrenceView();
                    addedFormSetOccurrenceView.onValidityChanged((event: RecordingValidityChangedEvent) => {
                        this.handleFormSetOccurrenceViewValidityChanged(event);
                    });
                }
            });
            this.formItemOccurrences.onOccurrenceRemoved((event: OccurrenceRemovedEvent) => {

                this.refresh();

                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getOccurrenceView(), FormItemSetOccurrenceView)) {
                    // force validate, since FormItemSet might have become invalid
                    this.validate(false);
                }
            });

            this.formItemOccurrences.getOccurrenceViews().forEach((formSetOccurrenceView: V)=> {
                formSetOccurrenceView.onValidityChanged((event: RecordingValidityChangedEvent) => {
                    this.handleFormSetOccurrenceViewValidityChanged(event);
                });
                formSetOccurrenceView.onEditContentRequest((summary: api.content.ContentSummary) => {
                    this.notifyEditContentRequested(summary);
                })
            });
        }

        private makeAddButton(): api.ui.button.Button {
            var addButton = new api.ui.button.Button("Add " + this.formSet.getLabel());
            addButton.addClass("small");
            addButton.onClicked((event: MouseEvent) => {
                this.formItemOccurrences.createAndAddOccurrence(this.formItemOccurrences.countOccurrences(), false);
                if ((<FormSetOccurrences<V>> this.formItemOccurrences).isCollapsed()) {
                    this.collapseButton.getHTMLElement().click();
                }
            });
            return addButton;
        }

        private makeCollapseButton(): api.dom.AEl {
            var collapseButton = new api.dom.AEl("collapse-button");
            collapseButton.setHtml("Collapse");
            collapseButton.onClicked((event: MouseEvent) => {
                if ((<FormSetOccurrences<V>> this.formItemOccurrences).isCollapsed()) {
                    collapseButton.setHtml("Collapse");
                    (<FormSetOccurrences<V>> this.formItemOccurrences).showOccurrences(true);
                } else {
                    collapseButton.setHtml("Expand");
                    (<FormSetOccurrences<V>> this.formItemOccurrences).showOccurrences(false);
                }
                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            return collapseButton;
        }

        protected handleFormSetOccurrenceViewValidityChanged(event: RecordingValidityChangedEvent) {

            if (!this.previousValidationRecording) {
                return; // skip handling if not previousValidationRecording is not set
            }
            var previousValidState = this.previousValidationRecording.isValid();
            if (event.isValid()) {
                this.previousValidationRecording.removeByPath(event.getOrigin(), false, event.isIncludeChildren());
            }
            else {
                this.previousValidationRecording.flatten(event.getRecording());
            }

            var validationRecordingPath = this.resolveValidationRecordingPath();

            var occurrenceViews = this.formItemOccurrences.getOccurrenceViews();
            var occurrenceRecording = new ValidationRecording(); // validity state of occurrences

            var numberOfValids = 0;
            occurrenceViews.forEach((occurrenceView: FormSetOccurrenceView) => {
                var recordingForOccurrence = occurrenceView.getValidationRecording();
                if (recordingForOccurrence) {
                    if (recordingForOccurrence.isValid()) {
                        numberOfValids++;
                    } else {
                        occurrenceRecording.flatten(recordingForOccurrence);
                    }
                }
            });

            // We ensure that previousValidationRecording is invalid both when: at least on of its occurrences is invalid
            // or number of occurrences breaks contract.

            if (numberOfValids < this.getOccurrences().getMinimum()) {
                this.previousValidationRecording.breaksMinimumOccurrences(validationRecordingPath);
            } else if (!occurrenceRecording.containsPathInBreaksMin(validationRecordingPath)) {
                this.previousValidationRecording.removeUnreachedMinimumOccurrencesByPath(validationRecordingPath, false);
            }

            if (this.getOccurrences().maximumBreached(numberOfValids)) {
                this.previousValidationRecording.breaksMaximumOccurrences(validationRecordingPath);
            } else if (!occurrenceRecording.containsPathInBreaksMax(validationRecordingPath)) {
                this.previousValidationRecording.removeBreachedMaximumOccurrencesByPath(validationRecordingPath, false);
            }

            this.renderValidationErrors(this.previousValidationRecording);

            if (previousValidState != this.previousValidationRecording.isValid()) {
                this.notifyValidityChanged(new RecordingValidityChangedEvent(this.previousValidationRecording,
                    validationRecordingPath).setIncludeChildren(true));
            }
        }

        protected getPropertyArray(propertySet: PropertySet): PropertyArray {
            throw new Error("Must be implemented by inheritor");
        }

        protected initOccurrences(): FormSetOccurrences<V> {
            throw new Error("Must be implemented by inheritor");
        }

        validate(silent: boolean = true, viewToSkipValidation: FormItemOccurrenceView = null): ValidationRecording {

            if (!this.formItemOccurrences) {
                throw new Error("Can't validate before layout is done");
            }

            var validationRecordingPath = this.resolveValidationRecordingPath(),
                wholeRecording = new ValidationRecording(),
                occurrenceViews = this.formItemOccurrences.getOccurrenceViews().filter(view => view != viewToSkipValidation),
                numberOfValids = 0;

            occurrenceViews.forEach((occurrenceView: FormSetOccurrenceView) => {
                var recordingForOccurrence = occurrenceView.validate(silent);
                if (recordingForOccurrence.isValid()) {
                    numberOfValids++;
                }
                wholeRecording.flatten(recordingForOccurrence);
            });

            if (numberOfValids < this.getOccurrences().getMinimum()) {
                wholeRecording.breaksMinimumOccurrences(validationRecordingPath);
            }
            if (this.getOccurrences().maximumBreached(numberOfValids)) {
                wholeRecording.breaksMaximumOccurrences(validationRecordingPath);
            }

            if (!silent && wholeRecording.validityChanged(this.previousValidationRecording)) {
                this.notifyValidityChanged(new RecordingValidityChangedEvent(wholeRecording, validationRecordingPath));
            }

            // display only errors related to occurrences
            this.renderValidationErrors(wholeRecording);

            this.previousValidationRecording = wholeRecording;

            return wholeRecording;
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

        protected handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {

            var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
            api.util.assert(draggedElement.hasClass(this.classPrefix + "-occurrence-view"));
            this.draggingIndex = draggedElement.getSiblingIndex();

            DragHelper.get().setDropAllowed(true);
            ui.placeholder.html("Drop form item set here");
        }

        protected handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            if (this.draggingIndex >= 0) {
                var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
                api.util.assert(draggedElement.hasClass(this.classPrefix + "-occurrence-view"));
                var draggedToIndex = draggedElement.getSiblingIndex();

                this.formItemOccurrences.moveOccurrence(this.draggingIndex, draggedToIndex);
            }

            this.draggingIndex = -1;
        }

        toggleHelpText(show?: boolean) {
            if (!!this.helpText) {
                this.formItemOccurrences.toggleHelpText(show);
            }
        }

        hasHelpText(): boolean {
            return !!this.helpText;
        }

        protected getOccurrences(): api.form.Occurrences {
            return this.formSet.getOccurrences();
        }

        protected resolveValidationRecordingPath(): ValidationRecordingPath {
            return new ValidationRecordingPath(this.parentDataSet.getPropertyPath(), this.formSet.getName(),
                this.getOccurrences().getMinimum(), this.getOccurrences().getMaximum());
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
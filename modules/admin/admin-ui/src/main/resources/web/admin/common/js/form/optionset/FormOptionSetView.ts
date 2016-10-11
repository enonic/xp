module api.form.optionset {

    import PropertySet = api.data.PropertySet;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export interface FormOptionSetViewConfig {

        context: FormContext;

        formOptionSet: FormOptionSet;

        parent: FormOptionSetOccurrenceView;

        parentDataSet: PropertySet;
    }

    export class FormOptionSetView extends FormItemView {

        private formOptionSet: FormOptionSet;

        private parentDataSet: PropertySet;

        private occurrenceViewsContainer: api.dom.DivEl;

        private formOptionSetOccurrences: FormOptionSetOccurrences;

        private bottomButtonRow: api.dom.DivEl;

        private addButton: api.ui.button.Button;

        private collapseButton: api.dom.AEl;

        private validityChangedListeners: {(event: RecordingValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: ValidationRecording;

        /**
         * The index of child Data being dragged.
         */
        private draggingIndex: number;

        constructor(config: FormOptionSetViewConfig) {
            super(<FormItemViewConfig> {
                className: "form-option-set-view",
                context: config.context,
                formItem: config.formOptionSet,
                parent: config.parent
            });
            this.parentDataSet = config.parentDataSet;
            this.formOptionSet = config.formOptionSet;

            this.addClass(this.formOptionSet.getPath().getElements().length % 2 ? "even" : "odd");
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
                placeholder: 'form-option-set-drop-target-placeholder',
                helper: (event, ui) => api.ui.DragHelper.get().getHTMLElement(),
                start: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStart(event, ui),
                update: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDUpdate(event, ui)
            });

            this.appendChild(this.occurrenceViewsContainer);

            var propertyArray = this.getPropertyArray(this.parentDataSet);

            this.formOptionSetOccurrences = new FormOptionSetOccurrences(<FormOptionSetOccurrencesConfig>{
                context: this.getContext(),
                occurrenceViewContainer: this.occurrenceViewsContainer,
                formOptionSet: this.formOptionSet,
                parent: this.getParent(),
                propertyArray: propertyArray
            });

            this.formOptionSetOccurrences.layout(validate).then(() => {

                this.subscribeFormOptionSetOccurrencesOnEvents();

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

        private getPropertyArray(parentPropertySet: PropertySet): PropertyArray {
            var existingPropertyArray = parentPropertySet.getPropertyArray(this.formOptionSet.getName());
            if (!existingPropertyArray) {
                parentPropertySet.addPropertySet(this.formOptionSet.getName());
            }
            var propertyArray = parentPropertySet.getPropertyArray(this.formOptionSet.getName());
            /*if (!propertyArray) {
             propertyArray = PropertyArray.create().
             setType(ValueTypes.DATA).
             setName(this.formOptionSet.getName()).
             setParent(this.parentDataSet).
             build();
             propertySet.addPropertyArray(propertyArray);
             }*/

            return propertyArray;
        }

        private subscribeFormOptionSetOccurrencesOnEvents() {

            this.formOptionSetOccurrences.onOccurrenceRendered((event: OccurrenceRenderedEvent) => {
                this.validate(false, event.validateViewOnRender() ? null : event.getOccurrenceView());
            });

            this.formOptionSetOccurrences.onOccurrenceAdded((event: OccurrenceAddedEvent) => {
                this.refresh();
                wemjq(this.occurrenceViewsContainer.getHTMLElement()).sortable("refresh");

                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getOccurrenceView(), FormOptionSetOccurrenceView)) {
                    var addedFormOptionSetOccurrenceView = <FormOptionSetOccurrenceView>event.getOccurrenceView();
                    addedFormOptionSetOccurrenceView.onValidityChanged((event: RecordingValidityChangedEvent) => {
                        this.handleFormOptionSetOccurrenceViewValidityChanged(event);
                    });
                }
            });
            this.formOptionSetOccurrences.onOccurrenceRemoved((event: OccurrenceRemovedEvent) => {

                this.refresh();

                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getOccurrenceView(), FormOptionSetOccurrenceView)) {
                    // force validate, since FormOptionSet might have become invalid
                    this.validate(false);
                }
            });

            this.formOptionSetOccurrences.getOccurrenceViews().forEach((formOptionSetOccurrenceView: FormOptionSetOccurrenceView)=> {
                formOptionSetOccurrenceView.onValidityChanged((event: RecordingValidityChangedEvent) => {
                    this.handleFormOptionSetOccurrenceViewValidityChanged(event);
                });
                formOptionSetOccurrenceView.onEditContentRequest((summary: api.content.ContentSummary) => {
                    this.notifyEditContentRequested(summary);
                })
            });
        }

        private makeAddButton(): api.ui.button.Button {
            var addButton = new api.ui.button.Button("Add " + this.formOptionSet.getLabel());
            addButton.addClass("small");
            addButton.onClicked((event: MouseEvent) => {
                this.formOptionSetOccurrences.createAndAddOccurrence(this.formOptionSetOccurrences.countOccurrences(), false);
                if (this.formOptionSetOccurrences.isCollapsed()) {
                    this.collapseButton.getHTMLElement().click();
                }

            });

            return addButton;
        }

        private makeCollapseButton(): api.dom.AEl {
            var collapseButton = new api.dom.AEl("collapse-button");
            collapseButton.setHtml("Collapse");
            collapseButton.onClicked((event: MouseEvent) => {
                if (this.formOptionSetOccurrences.isCollapsed()) {
                    collapseButton.setHtml("Collapse");
                    this.formOptionSetOccurrences.showOccurrences(true);
                } else {
                    collapseButton.setHtml("Expand");
                    this.formOptionSetOccurrences.showOccurrences(false);
                }
                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            return collapseButton;
        }

        update(propertySet: api.data.PropertySet, unchangedOnly?: boolean): Q.Promise<void> {
            this.parentDataSet = propertySet;
            var propertyArray = this.getPropertyArray(propertySet);
            return this.formOptionSetOccurrences.update(propertyArray, unchangedOnly);
        }

        private handleFormOptionSetOccurrenceViewValidityChanged(event: RecordingValidityChangedEvent) {

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

            var occurrenceViews = this.formOptionSetOccurrences.getOccurrenceViews();
            var occurrenceRecording = new ValidationRecording(); // validity state of occurrences

            var numberOfValids = 0;
            occurrenceViews.forEach((occurrenceView: FormOptionSetOccurrenceView) => {
                var recordingForOccurrence = occurrenceView.getValidationRecording();
                if (recordingForOccurrence) {
                    if (recordingForOccurrence.isValid()) {
                        numberOfValids++;
                    } else {
                        occurrenceRecording.flatten(recordingForOccurrence);
                    }
                }
            });

            // We ensure that previousValidationRecording is invalid both when at least on of its occurrences is invalid
            // or number of occurrences breaks contract.

            if (numberOfValids < this.formOptionSet.getOccurrences().getMinimum()) {
                this.previousValidationRecording.breaksMinimumOccurrences(validationRecordingPath);
            } else if (!occurrenceRecording.containsPathInBreaksMin(validationRecordingPath)) {
                this.previousValidationRecording.removeUnreachedMinimumOccurrencesByPath(validationRecordingPath, false);
            }

            if (this.formOptionSet.getOccurrences().maximumBreached(numberOfValids)) {
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

        broadcastFormSizeChanged() {
            this.formOptionSetOccurrences.getOccurrenceViews().forEach((occurrenceView: FormOptionSetOccurrenceView) => {
                occurrenceView.getFormItemViews().forEach((formItemView: FormItemView) => {
                    formItemView.broadcastFormSizeChanged();
                });
            });
        }

        refresh() {
            this.collapseButton.setVisible(this.formOptionSetOccurrences.getOccurrences().length > 0);
            this.addButton.setVisible(!this.formOptionSetOccurrences.maximumOccurrencesReached());
        }

        private resolveValidationRecordingPath(): ValidationRecordingPath {

            return new ValidationRecordingPath(this.parentDataSet.getPropertyPath(), this.formOptionSet.getName(),
                this.formOptionSet.getOccurrences().getMinimum(), this.formOptionSet.getOccurrences().getMaximum());
        }

        public displayValidationErrors(value: boolean) {
            this.formOptionSetOccurrences.getOccurrenceViews().forEach((view: FormOptionSetOccurrenceView) => {
                view.displayValidationErrors(value);
            });
        }

        public setHighlightOnValidityChange(highlight: boolean) {
            this.formOptionSetOccurrences.getOccurrenceViews().forEach((view: FormOptionSetOccurrenceView) => {
                view.setHighlightOnValidityChange(highlight);
            });
        }

        hasValidUserInput(): boolean {

            var result = true;
            this.formOptionSetOccurrences.getOccurrenceViews().forEach((formItemOccurrenceView: FormItemOccurrenceView) => {
                if (!formItemOccurrenceView.hasValidUserInput()) {
                    result = false;
                }
            });

            return result;
        }

        validate(silent: boolean = true, viewToSkipValidation: FormItemOccurrenceView = null): ValidationRecording {

            var validationRecordingPath = this.resolveValidationRecordingPath(),
                wholeRecording = new ValidationRecording(),
                occurrenceViews = this.formOptionSetOccurrences.getOccurrenceViews().filter(view => view != viewToSkipValidation),
                numberOfValids = 0;

            occurrenceViews.forEach((occurrenceView: FormOptionSetOccurrenceView) => {
                var recordingForOccurrence = occurrenceView.validate(silent);
                if (recordingForOccurrence.isValid()) {
                    numberOfValids++;
                }
                wholeRecording.flatten(recordingForOccurrence);
            });

            if (numberOfValids < this.formOptionSet.getOccurrences().getMinimum()) {
                wholeRecording.breaksMinimumOccurrences(validationRecordingPath);
            }
            if (this.formOptionSet.getOccurrences().maximumBreached(numberOfValids)) {
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

        onValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            this.validityChangedListeners.filter((currentListener: (event: RecordingValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValidityChanged(event: RecordingValidityChangedEvent) {
            this.validityChangedListeners.forEach((listener: (event: RecordingValidityChangedEvent)=>void) => {
                listener(event);
            });
        }

        private renderValidationErrors(recording: ValidationRecording) {
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
            if (this.formOptionSetOccurrences.getOccurrenceViews().length > 0) {
                var views: FormItemOccurrenceView[] = this.formOptionSetOccurrences.getOccurrenceViews();
                for (var i = 0; i < views.length; i++) {
                    if (views[i].giveFocus()) {
                        focusGiven = true;
                        break;
                    }
                }
            }
            return focusGiven;
        }

        private handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {

            var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
            api.util.assert(draggedElement.hasClass("form-option-set-occurrence-view"));
            this.draggingIndex = draggedElement.getSiblingIndex();

            ui.placeholder.html("Drop form option set here");
        }

        private handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            if (this.draggingIndex >= 0) {
                var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
                api.util.assert(draggedElement.hasClass("form-option-set-occurrence-view"));
                var draggedToIndex = draggedElement.getSiblingIndex();

                this.formOptionSetOccurrences.moveOccurrence(this.draggingIndex, draggedToIndex);
            }

            this.draggingIndex = -1;
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.formOptionSetOccurrences.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.formOptionSetOccurrences.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.formOptionSetOccurrences.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.formOptionSetOccurrences.unBlur(listener);
        }
    }
}
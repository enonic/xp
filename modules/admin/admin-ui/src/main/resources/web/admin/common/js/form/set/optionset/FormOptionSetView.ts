module api.form {

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

    export class FormOptionSetView extends FormSetView<FormOptionSetOccurrenceView> {

        private formOptionSet: FormOptionSet;

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
            if (this.formOptionSet.getOccurrences().getMaximum() == 1) {
                this.addClass("max-1-occurrence");
            }
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

            this.formItemOccurrences = new FormOptionSetOccurrences(<FormOptionSetOccurrencesConfig>{
                context: this.getContext(),
                occurrenceViewContainer: this.occurrenceViewsContainer,
                formOptionSet: this.formOptionSet,
                parent: this.getParent(),
                propertyArray: propertyArray
            });

            this.formItemOccurrences.layout(validate).then(() => {

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

        protected getPropertyArray(parentPropertySet: PropertySet): PropertyArray {
            var propertyArray = parentPropertySet.getPropertyArray(this.formOptionSet.getName());
            if (!propertyArray) {
                propertyArray = PropertyArray.create().setType(ValueTypes.DATA).setName(this.formOptionSet.getName()).setParent(
                    this.parentDataSet).build();
                parentPropertySet.addPropertyArray(propertyArray);
            }
            return propertyArray;
        }

        private subscribeFormOptionSetOccurrencesOnEvents() {

            this.formItemOccurrences.onOccurrenceRendered((event: OccurrenceRenderedEvent) => {
                this.validate(false, event.validateViewOnRender() ? null : event.getOccurrenceView());
            });

            this.formItemOccurrences.onOccurrenceAdded((event: OccurrenceAddedEvent) => {
                this.refresh();
                wemjq(this.occurrenceViewsContainer.getHTMLElement()).sortable("refresh");

                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getOccurrenceView(), FormOptionSetOccurrenceView)) {
                    var addedFormOptionSetOccurrenceView = <FormOptionSetOccurrenceView>event.getOccurrenceView();
                    addedFormOptionSetOccurrenceView.onValidityChanged((event: RecordingValidityChangedEvent) => {
                        this.handleFormOptionSetOccurrenceViewValidityChanged(event);
                    });
                }
            });
            this.formItemOccurrences.onOccurrenceRemoved((event: OccurrenceRemovedEvent) => {

                this.refresh();

                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getOccurrenceView(), FormOptionSetOccurrenceView)) {
                    // force validate, since FormOptionSet might have become invalid
                    this.validate(false);
                }
            });

            this.formItemOccurrences.getOccurrenceViews().forEach((formOptionSetOccurrenceView: FormOptionSetOccurrenceView)=> {
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
                this.formItemOccurrences.createAndAddOccurrence(this.formItemOccurrences.countOccurrences(), false);
                if ((<FormOptionSetOccurrences> this.formItemOccurrences).isCollapsed()) {
                    this.collapseButton.getHTMLElement().click();
                }

            });

            return addButton;
        }

        private makeCollapseButton(): api.dom.AEl {
            var collapseButton = new api.dom.AEl("collapse-button");
            collapseButton.setHtml("Collapse");
            collapseButton.onClicked((event: MouseEvent) => {
                if ((<FormOptionSetOccurrences> this.formItemOccurrences).isCollapsed()) {
                    collapseButton.setHtml("Collapse");
                    (<FormOptionSetOccurrences> this.formItemOccurrences).showOccurrences(true);
                } else {
                    collapseButton.setHtml("Expand");
                    (<FormOptionSetOccurrences> this.formItemOccurrences).showOccurrences(false);
                }
                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            return collapseButton;
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

            var occurrenceViews = this.formItemOccurrences.getOccurrenceViews();
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

            // We ensure that previousValidationRecording is invalid both when: at least on of its occurrences is invalid
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

        private resolveValidationRecordingPath(): ValidationRecordingPath {

            return new ValidationRecordingPath(this.parentDataSet.getPropertyPath(), this.formOptionSet.getName(),
                this.formOptionSet.getOccurrences().getMinimum(), this.formOptionSet.getOccurrences().getMaximum());
        }

        toggleHelpText(show?: boolean) {
            if (!!this.formOptionSet.getHelpText()) {
                this.formItemOccurrences.toggleHelpText(show);
            }
        }

        hasHelpText(): boolean {
            return !!this.formOptionSet.getHelpText();
        }

        validate(silent: boolean = true, viewToSkipValidation: FormItemOccurrenceView = null): ValidationRecording {

            var validationRecordingPath = this.resolveValidationRecordingPath(),
                wholeRecording = new ValidationRecording(),
                occurrenceViews = this.formItemOccurrences.getOccurrenceViews().filter(view => view != viewToSkipValidation),
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

        protected handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {

            var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
            api.util.assert(draggedElement.hasClass("form-option-set-occurrence-view"));
            this.draggingIndex = draggedElement.getSiblingIndex();

            ui.placeholder.html("Drop form option set here");
        }

        protected handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            if (this.draggingIndex >= 0) {
                var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
                api.util.assert(draggedElement.hasClass("form-option-set-occurrence-view"));
                var draggedToIndex = draggedElement.getSiblingIndex();

                this.formItemOccurrences.moveOccurrence(this.draggingIndex, draggedToIndex);
            }

            this.draggingIndex = -1;
        }

    }
}
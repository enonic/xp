module api.form {

    import PropertySet = api.data.PropertySet;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export interface FormItemSetViewConfig {

        context: FormContext;

        formItemSet: FormItemSet;

        parent: FormItemSetOccurrenceView;

        parentDataSet: PropertySet;
    }

    export class FormItemSetView extends FormSetView<FormItemSetOccurrenceView> {

        private formItemSet: FormItemSet;

        constructor(config: FormItemSetViewConfig) {
            super(<FormItemViewConfig> {
                className: "form-item-set-view",
                context: config.context,
                formItem: config.formItemSet,
                parent: config.parent
            });
            this.parentDataSet = config.parentDataSet;
            this.formItemSet = config.formItemSet;

            this.addClass(this.formItemSet.getPath().getElements().length % 2 ? "even" : "odd");
        }

        protected getPropertyArray(propertySet: PropertySet): PropertyArray {
            var propertyArray = propertySet.getPropertyArray(this.formItemSet.getName());
            if (!propertyArray) {
                propertyArray = PropertyArray.create().
                    setType(ValueTypes.DATA).
                    setName(this.formItemSet.getName()).
                    setParent(this.parentDataSet).
                    build();
                propertySet.addPropertyArray(propertyArray);
            }
            return propertyArray;
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
                placeholder: 'form-item-set-drop-target-placeholder',
                helper: (event, ui) => api.ui.DragHelper.get().getHTMLElement(),
                start: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStart(event, ui),
                update: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDUpdate(event, ui)
            });

            this.appendChild(this.occurrenceViewsContainer);

            this.formItemOccurrences = new FormItemSetOccurrences(<FormItemSetOccurrencesConfig>{
                context: this.getContext(),
                occurrenceViewContainer: this.occurrenceViewsContainer,
                formItemSet: this.formItemSet,
                parent: this.getParent(),
                propertyArray: this.getPropertyArray(this.parentDataSet)
            });

            this.formItemOccurrences.layout(validate).then(() => {

                this.subscribeFormItemSetOccurrencesOnEvents();

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

        private subscribeFormItemSetOccurrencesOnEvents() {

            this.formItemOccurrences.onOccurrenceRendered((event: OccurrenceRenderedEvent) => {
                this.validate(false, event.validateViewOnRender() ? null : event.getOccurrenceView());
            });

            this.formItemOccurrences.onOccurrenceAdded((event: OccurrenceAddedEvent) => {
                this.refresh();
                wemjq(this.occurrenceViewsContainer.getHTMLElement()).sortable("refresh");

                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getOccurrenceView(), FormItemSetOccurrenceView)) {
                    var addedFormItemSetOccurrenceView = <FormItemSetOccurrenceView>event.getOccurrenceView();
                    addedFormItemSetOccurrenceView.onValidityChanged((event: RecordingValidityChangedEvent) => {
                        this.handleFormItemSetOccurrenceViewValidityChanged(event);
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

            this.formItemOccurrences.getOccurrenceViews().forEach((formItemSetOccurrenceView: FormItemSetOccurrenceView)=> {
                formItemSetOccurrenceView.onValidityChanged((event: RecordingValidityChangedEvent) => {
                    this.handleFormItemSetOccurrenceViewValidityChanged(event);
                });
                formItemSetOccurrenceView.onEditContentRequest((summary: api.content.ContentSummary) => {
                    this.notifyEditContentRequested(summary);
                })
            });
        }

        private makeAddButton(): api.ui.button.Button {
            var addButton = new api.ui.button.Button("Add " + this.formItemSet.getLabel());
            addButton.addClass("small");
            addButton.onClicked((event: MouseEvent) => {
                this.formItemOccurrences.createAndAddOccurrence(this.formItemOccurrences.countOccurrences(), false);
                if ((<FormItemSetOccurrences> this.formItemOccurrences).isCollapsed()) {
                    this.collapseButton.getHTMLElement().click();
                }

            });

            return addButton;
        }

        private makeCollapseButton(): api.dom.AEl {
            var collapseButton = new api.dom.AEl("collapse-button");
            collapseButton.setHtml("Collapse");
            collapseButton.onClicked((event: MouseEvent) => {
                if ((<FormItemSetOccurrences> this.formItemOccurrences).isCollapsed()) {
                    collapseButton.setHtml("Collapse");
                    (<FormItemSetOccurrences> this.formItemOccurrences).showOccurrences(true);
                } else {
                    collapseButton.setHtml("Expand");
                    (<FormItemSetOccurrences> this.formItemOccurrences).showOccurrences(false);
                }
                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            return collapseButton;
        }

        private handleFormItemSetOccurrenceViewValidityChanged(event: RecordingValidityChangedEvent) {

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
            var occurrenceRecording = new ValidationRecording();

            var numberOfValids = 0;
            occurrenceViews.forEach((occurrenceView: FormItemSetOccurrenceView) => {
                var recordingForOccurrence = occurrenceView.getValidationRecording();
                if (recordingForOccurrence && recordingForOccurrence.isValid()) {
                    numberOfValids++;
                }
            });

            if (numberOfValids < this.formItemSet.getOccurrences().getMinimum()) {
                this.previousValidationRecording.breaksMinimumOccurrences(validationRecordingPath);
                occurrenceRecording.breaksMinimumOccurrences(validationRecordingPath);
            } else {
                this.previousValidationRecording.removeUnreachedMinimumOccurrencesByPath(validationRecordingPath, true);
            }

            if (this.formItemSet.getOccurrences().maximumBreached(numberOfValids)) {
                this.previousValidationRecording.breaksMaximumOccurrences(validationRecordingPath);
                occurrenceRecording.breaksMaximumOccurrences(validationRecordingPath);
            } else {
                this.previousValidationRecording.removeBreachedMaximumOccurrencesByPath(validationRecordingPath, true);
            }

            this.renderValidationErrors(occurrenceRecording);

            if (previousValidState != this.previousValidationRecording.isValid()) {
                this.notifyValidityChanged(new RecordingValidityChangedEvent(this.previousValidationRecording,
                    validationRecordingPath).setIncludeChildren(true));
            }
        }

        private resolveValidationRecordingPath(): ValidationRecordingPath {

            return new ValidationRecordingPath(this.parentDataSet.getPropertyPath(), this.formItemSet.getName(),
                this.formItemSet.getOccurrences().getMinimum(), this.formItemSet.getOccurrences().getMaximum());
        }

        validate(silent: boolean = true, viewToSkipValidation: FormItemOccurrenceView = null): ValidationRecording {

            var validationRecordingPath = this.resolveValidationRecordingPath();

            var wholeRecording = new ValidationRecording();

            var occurrenceViews = this.formItemOccurrences.getOccurrenceViews().filter(view => view != viewToSkipValidation);
            var occurrenceRecording = new ValidationRecording();

            var numberOfValids = 0;
            occurrenceViews.forEach((occurrenceView: FormItemSetOccurrenceView) => {
                var recordingForOccurrence = occurrenceView.validate(silent);
                if (recordingForOccurrence.isValid()) {
                    numberOfValids++;
                }
                wholeRecording.flatten(recordingForOccurrence);
            });

            if (numberOfValids < this.formItemSet.getOccurrences().getMinimum()) {
                wholeRecording.breaksMinimumOccurrences(validationRecordingPath);
                occurrenceRecording.breaksMinimumOccurrences(validationRecordingPath);
            }
            if (this.formItemSet.getOccurrences().maximumBreached(numberOfValids)) {
                wholeRecording.breaksMaximumOccurrences(validationRecordingPath);
                occurrenceRecording.breaksMaximumOccurrences(validationRecordingPath);
            }

            if (!silent && wholeRecording.validityChanged(this.previousValidationRecording)) {
                this.notifyValidityChanged(new RecordingValidityChangedEvent(wholeRecording, validationRecordingPath));
            }

            // display only errors related to occurrences
            this.renderValidationErrors(occurrenceRecording);

            this.previousValidationRecording = wholeRecording;

            return wholeRecording;
        }

        private handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {

            var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
            api.util.assert(draggedElement.hasClass("form-item-set-occurrence-view"));
            this.draggingIndex = draggedElement.getSiblingIndex();

            ui.placeholder.html("Drop form item set here");
        }

        private handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            if (this.draggingIndex >= 0) {
                var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
                api.util.assert(draggedElement.hasClass("form-item-set-occurrence-view"));
                var draggedToIndex = draggedElement.getSiblingIndex();

                this.formItemOccurrences.moveOccurrence(this.draggingIndex, draggedToIndex);
            }

            this.draggingIndex = -1;
        }

        toggleHelpText(show?: boolean) {
            if (!!this.formItemSet.getHelpText()) {
                this.formItemSet.toggleHelpText(show);
                this.formItemOccurrences.getOccurrenceViews().forEach((formSetOccurrenceView: FormSetOccurrenceView) => {
                    formSetOccurrenceView.toggleHelpText(show);
                })
            }
        }

        hasHelpText(): boolean {
            return !!this.formItemSet.getHelpText();
        }

    }
}
module api.form {

    export interface FormItemSetViewConfig {

        context: FormContext;

        formItemSet: FormItemSet;

        parent: FormItemSetOccurrenceView;

        parentDataSet: api.data.DataSet;
    }

    export class FormItemSetView extends FormItemView {

        private formItemSet: FormItemSet;

        private parentDataSet: api.data.DataSet;

        private occurrenceViewsContainer: api.dom.DivEl;

        private formItemSetOccurrences: FormItemSetOccurrences;

        private bottomButtonRow: api.dom.DivEl;

        private addButton: api.ui.button.Button;

        private collapseButton: api.dom.AEl;

        private validationViewer: api.form.ValidationRecordingViewer;

        private validityChangedListeners: {(event: ValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: ValidationRecording;

        /**
         * The index of child Data being dragged.
         */
        private draggingIndex: number;

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
                helper: (event, helper) => api.ui.DragHelper.getHtml(),
                start: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStart(event, ui),
                update: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDUpdate(event, ui)
            });

            this.appendChild(this.occurrenceViewsContainer);


            this.formItemSetOccurrences =
            new FormItemSetOccurrences(<FormItemSetOccurrencesConfig>{
                context: this.getContext(),
                occurrenceViewContainer: this.occurrenceViewsContainer,
                formItemSet: config.formItemSet,
                parent: this.getParent(),
                parentDataSet: this.parentDataSet
            });
            this.formItemSetOccurrences.layout();

            this.formItemSetOccurrences.onOccurrenceAdded((event: OccurrenceAddedEvent) => {
                this.refresh();
                wemjq(this.occurrenceViewsContainer.getHTMLElement()).sortable("refresh");

                if (event.getOccurrenceView() instanceof FormItemSetOccurrenceView) {
                    var addedFormItemSetOccurrenceView = <FormItemSetOccurrenceView>event.getOccurrenceView();
                    addedFormItemSetOccurrenceView.onValidityChanged((event: ValidityChangedEvent) => {
                        this.handleFormItemSetOccurrenceViewValidityChanged(event);
                    });
                }
            });
            this.formItemSetOccurrences.onOccurrenceRemoved((event: OccurrenceRemovedEvent) => {

                var dataId = new api.data.DataId(this.formItemSet.getName(), event.getOccurrence().getIndex());
                this.parentDataSet.removeData(dataId);

                this.refresh();

                if (event.getOccurrenceView() instanceof FormItemSetOccurrenceView) {
                    // force validate, since FormItemSet might have become invalid
                    this.validate(false);
                }
            });

            this.formItemSetOccurrences.getOccurrenceViews().forEach((formItemSetOccurrenceView: FormItemSetOccurrenceView)=> {
                formItemSetOccurrenceView.onValidityChanged((event: ValidityChangedEvent) => {
                    this.handleFormItemSetOccurrenceViewValidityChanged(event);
                });
            });
            this.bottomButtonRow = new api.dom.DivEl("bottom-button-row");
            this.appendChild(this.bottomButtonRow);

            this.addButton = new api.ui.button.Button("Add " + this.formItemSet.getLabel());
            this.addButton.addClass("small");
            this.addButton.onClicked((event: MouseEvent) => {
                this.formItemSetOccurrences.createAndAddOccurrence();
                if (this.formItemSetOccurrences.isCollapsed()) {
                    this.collapseButton.getHTMLElement().click();
                }

            });
            this.collapseButton = new api.dom.AEl("collapse-button");
            this.collapseButton.setHtml("Collapse");
            this.collapseButton.onClicked((event: MouseEvent) => {
                if (this.formItemSetOccurrences.isCollapsed()) {
                    this.collapseButton.setHtml("Collapse");
                    this.formItemSetOccurrences.showOccurences(true);
                } else {
                    this.collapseButton.setHtml("Expand");
                    this.formItemSetOccurrences.showOccurences(false);
                }

            });

            this.bottomButtonRow.appendChild(this.addButton);
            this.bottomButtonRow.appendChild(this.collapseButton);

            this.validationViewer = new api.form.ValidationRecordingViewer();
            this.appendChild(this.validationViewer);

            this.refresh();
            this.validate(true);
        }

        private handleFormItemSetOccurrenceViewValidityChanged(event: ValidityChangedEvent) {

            if (!this.previousValidationRecording) {
                return; // skip handling if not previousValidationRecording is not set
            }
            var previousValidState = this.previousValidationRecording.isValid();
            if (event.isValid()) {
                this.previousValidationRecording.removeByPath(event.getOrigin());
            }
            else {
                this.previousValidationRecording.flatten(event.getRecording());
            }

            var validationRecordingPath = this.resolveValidationRecordingPath();

            var occurrenceViews = this.formItemSetOccurrences.getOccurrenceViews();
            var occurrenceRecording = new ValidationRecording();

            var numberOfValids = 0;
            occurrenceViews.forEach((occurrenceView: FormItemSetOccurrenceView) => {
                var recordingForOccurrence = occurrenceView.getLastValidationRecording();
                if (recordingForOccurrence.isValid()) {
                    numberOfValids++;
                }
            });

            if (numberOfValids < this.formItemSet.getOccurrences().getMinimum()) {
                this.previousValidationRecording.breaksMinimumOccurrences(validationRecordingPath);
                occurrenceRecording.breaksMinimumOccurrences(validationRecordingPath);
            } else {
                this.previousValidationRecording.removeUnreachedMinimumOccurrencesByPath(validationRecordingPath);
            }

            if (this.formItemSet.getOccurrences().maximumBreached(numberOfValids)) {
                this.previousValidationRecording.breaksMaximumOccurrences(validationRecordingPath);
                occurrenceRecording.breaksMaximumOccurrences(validationRecordingPath);
            } else {
                this.previousValidationRecording.removeBreachedMaximumOccurrencesByPath(validationRecordingPath);
            }

            this.renderValidationErrors(occurrenceRecording);

            if (previousValidState != this.previousValidationRecording.isValid()) {
                this.notifyValidityChanged(new ValidityChangedEvent(this.previousValidationRecording,
                    this.resolveValidationRecordingPath()));
            }
        }

        broadcastFormSizeChanged() {
            this.formItemSetOccurrences.getOccurrenceViews().forEach((occurrenceView: FormItemSetOccurrenceView) => {
                occurrenceView.getFormItemViews().forEach((formItemView: FormItemView) => {
                    formItemView.broadcastFormSizeChanged();
                });
            });
        }

        refresh() {
            this.collapseButton.setVisible(this.formItemSetOccurrences.getOccurrences().length > 0);
            this.addButton.setVisible(!this.formItemSetOccurrences.maximumOccurrencesReached());
        }

        public getFormItemSetOccurrenceView(index: number): FormItemSetOccurrenceView {
            return this.formItemSetOccurrences.getOccurrenceViews()[index];
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return this.formItemSetOccurrences.getAttachments();
        }

        private resolveValidationRecordingPath(): ValidationRecordingPath {

            return new ValidationRecordingPath(this.getParentDataPath(), this.formItemSet.getName(),
                this.formItemSet.getOccurrences().getMinimum(), this.formItemSet.getOccurrences().getMaximum());
        }

        validate(silent: boolean = true): ValidationRecording {

            var validationRecordingPath = this.resolveValidationRecordingPath();
            //console.log("FormItemSetView[ " + validationRecordingPath + " ].validate(" + silent + ")");

            var wholeRecording = new ValidationRecording();

            var occurrenceViews = this.formItemSetOccurrences.getOccurrenceViews();
            var occurenceRecording = new ValidationRecording();

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
                occurenceRecording.breaksMinimumOccurrences(validationRecordingPath);
            }
            if (this.formItemSet.getOccurrences().maximumBreached(numberOfValids)) {
                wholeRecording.breaksMaximumOccurrences(validationRecordingPath);
                occurenceRecording.breaksMaximumOccurrences(validationRecordingPath);
            }

            if (!silent && wholeRecording.validityChanged(this.previousValidationRecording)) {
                this.notifyValidityChanged(new ValidityChangedEvent(wholeRecording, validationRecordingPath));
            }

            // display only errors related to occurences
            this.renderValidationErrors(occurenceRecording);

            this.previousValidationRecording = wholeRecording;

            return wholeRecording;
        }

        onValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.validityChangedListeners.filter((currentListener: (event: ValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValidityChanged(event: ValidityChangedEvent) {
            this.validityChangedListeners.forEach((listener: (event: ValidityChangedEvent)=>void) => {
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
            this.validationViewer.setObject(recording);
        }

        giveFocus(): boolean {

            var focusGiven = false;
            if (this.formItemSetOccurrences.getOccurrenceViews().length > 0) {
                var views: FormItemOccurrenceView[] = this.formItemSetOccurrences.getOccurrenceViews();
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
            api.util.assert(draggedElement.hasClass("form-item-set-occurrence-view"));
            this.draggingIndex = draggedElement.getSiblingIndex();

            ui.placeholder.html("Drop form item set here");
        }

        private handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            if (this.draggingIndex >= 0) {
                var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
                api.util.assert(draggedElement.hasClass("form-item-set-occurrence-view"));
                var draggedToIndex = draggedElement.getSiblingIndex();

                this.handleMovedOccurrence(this.draggingIndex, draggedToIndex);
            }

            this.draggingIndex = -1;
        }

        private handleMovedOccurrence(index: number, destinationIndex: number) {

            this.formItemSetOccurrences.moveOccurrence(index, destinationIndex);
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.formItemSetOccurrences.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.formItemSetOccurrences.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.formItemSetOccurrences.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.formItemSetOccurrences.unBlur(listener);
        }
    }
}
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

        private addButton: api.ui.Button;

        private collapseButton: api.dom.AEl;

        private validityChangedListeners: {(event: ValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: ValidationRecording;

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

            jQuery(this.occurrenceViewsContainer.getHTMLElement()).sortable({
                revert: false,
                containment: this.getHTMLElement(),
                cursor: 'move',
                cursorAt: {left: 14, top: 14},
                distance: 20,
                zIndex: 10000,
                tolerance: 'pointer',
                handle: '.drag-control',
                placeholder: 'form-item-set-drop-target-placeholder',
                helper: (event, helper) => this.createDnDHelper(),
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

            this.validate(true);
            this.formItemSetOccurrences.onOccurrenceAdded((event: OccurrenceAddedEvent) => {
                this.refresh();

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

            this.addButton = new api.ui.Button("Add " + this.formItemSet.getLabel());
            this.addButton.addClass("small");
            this.addButton.onClicked((event: MouseEvent) => {
                this.formItemSetOccurrences.createAndAddOccurrence();
                if (this.formItemSetOccurrences.isCollapsed()) {
                    this.collapseButton.getHTMLElement().click();
                }

            });
            this.collapseButton = new api.dom.AEl("collapse-button");
            this.collapseButton.setText("Collapse");
            this.collapseButton.onClicked((event: MouseEvent) => {
                if (this.formItemSetOccurrences.isCollapsed()) {
                    this.collapseButton.setText("Collapse");
                    this.formItemSetOccurrences.showOccurences(true);
                } else {
                    this.collapseButton.setText("Expand");
                    this.formItemSetOccurrences.showOccurences(false);
                }

            });

            this.bottomButtonRow.appendChild(this.addButton);
            this.bottomButtonRow.appendChild(this.collapseButton);
            this.refresh();
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
            var numberOfValids = 0;
            occurrenceViews.forEach((occurrenceView: FormItemSetOccurrenceView) => {
                var recordingForOccurrence = occurrenceView.getLastValidationRecording();
                if (recordingForOccurrence.isValid()) {
                    numberOfValids++;
                }
            });
            if (numberOfValids < this.formItemSet.getOccurrences().getMinimum()) {
                this.previousValidationRecording.breaksMinimumOccurrences(validationRecordingPath);
            }
            else {
                this.previousValidationRecording.removeUnreachedMinimumOccurrencesByPath(validationRecordingPath);
            }
            if (this.formItemSet.getOccurrences().maximumBreached(numberOfValids)) {
                this.previousValidationRecording.breaksMaximumOccurrences(validationRecordingPath);
            }
            else {
                this.previousValidationRecording.removeBreachedMaximumOccurrencesByPath(validationRecordingPath);
            }

            if (this.previousValidationRecording.isValid()) {
                this.removeClass("invalid");
                this.addClass("valid");
            }
            else {
                this.removeClass("valid");
                this.addClass("invalid");
            }

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

            return new ValidationRecordingPath(this.getParentDataPath(), this.formItemSet.getName());
        }

        validate(silent: boolean = true): ValidationRecording {

            var validationRecordingPath = this.resolveValidationRecordingPath();
            //console.log("FormItemSetView[ " + validationRecordingPath + " ].validate(" + silent + ")");

            var recording = new ValidationRecording();

            var occurrenceViews = this.formItemSetOccurrences.getOccurrenceViews();

            var numberOfValids = 0;
            occurrenceViews.forEach((occurrenceView: FormItemSetOccurrenceView) => {
                var recordingForOccurrence = occurrenceView.validate(silent);
                if (recordingForOccurrence.isValid()) {
                    numberOfValids++;
                }
                recording.flatten(recordingForOccurrence);
            });

            if (numberOfValids < this.formItemSet.getOccurrences().getMinimum()) {
                recording.breaksMinimumOccurrences(validationRecordingPath);
            }
            if (this.formItemSet.getOccurrences().maximumBreached(numberOfValids)) {
                recording.breaksMaximumOccurrences(validationRecordingPath);
            }

            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new ValidityChangedEvent(recording, validationRecordingPath));
                }
            }

            this.previousValidationRecording = recording;

            if (this.previousValidationRecording.isValid()) {
                this.removeClass("invalid");
                this.addClass("valid");
            }
            else {
                this.removeClass("valid");
                this.addClass("invalid");
            }

            return recording;
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

            /*console.log("FormItemSetView[ " + event.getOrigin().toString() + " ] validity changed");
             if (event.getRecording().isValid()) {
             console.log(" valid!");
             }
             else {
             console.log(" invalid: ");
             event.getRecording().print();
             }*/

            this.validityChangedListeners.forEach((listener: (event: ValidityChangedEvent)=>void) => {
                listener(event);
            });
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

        private createDnDHelper(): string {
            var div = new api.dom.DivEl();
            div.getEl()
                .setId("drag-helper")
                .addClass("form-item-set-drop-allowed")
                .setHeight("48px")
                .setWidth("48px")
                .setPosition("absolute")
                .setZindex(400000);
            return div.toString();
        }

        private handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {
            ui.placeholder.html("Drop form item set here");
        }

        private handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            var occurrenceOrderAccordingToDOM = this.resolveOccurrencesInOrderAccordingToDOM();

            // Update index of each occurrence
            occurrenceOrderAccordingToDOM.forEach((occurrence: FormItemSetOccurrence, index: number) => {
                occurrence.setIndex(index);
            });

            this.formItemSetOccurrences.reorderOccurrencesAccordingToNewIndexOrder();
        }

        private resolveOccurrencesInOrderAccordingToDOM(): FormItemSetOccurrence[] {
            var occurrencesInOrderAccordingToDOM: FormItemSetOccurrence[] = [];

            var formItemSetViewChildren = this.occurrenceViewsContainer.getHTMLElement().children;
            for (var i = 0; i < formItemSetViewChildren.length; i++) {
                var child = <HTMLElement> formItemSetViewChildren[i];
                var occurrenceView = this.formItemSetOccurrences.getOccurrenceViewById(child.id);
                if (occurrenceView) {
                    occurrencesInOrderAccordingToDOM.push(<FormItemSetOccurrence> occurrenceView.getOccurrence());
                }
            }

            return occurrencesInOrderAccordingToDOM;
        }

    }
}
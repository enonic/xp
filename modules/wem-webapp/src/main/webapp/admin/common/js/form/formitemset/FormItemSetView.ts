module api.form.formitemset {

    import support = api.form.inputtype.support;
    import ValidationRecorder = api.form.ValidationRecorder;

    export class FormItemSetView extends api.form.FormItemView {

        private formItemSet: api.form.FormItemSet;

        private dataSets: api.data.DataSet[];

        private occurrenceViewsContainer: api.dom.DivEl;

        private formItemSetOccurrences: FormItemSetOccurrences;

        private bottomButtonRow: api.dom.DivEl;

        private addButton: api.ui.Button;

        private collapseButton: api.dom.AEl;

        private listeners: {[eventName:string]:{(event: support.InputTypeEvent):void}[]} = {};

        private previousValidationRecording: api.form.ValidationRecorder;

        constructor(context: api.form.FormContext, formItemSet: api.form.FormItemSet, dataSets?: api.data.DataSet[]) {
            super("form-item-set-view", context, formItemSet);

            this.listeners[support.InputTypeEvents.ValidityChanged] = [];
            this.formItemSet = formItemSet;
            this.dataSets = dataSets != null ? dataSets : [];

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
                start: (event, ui) => this.handleDnDStart(event, ui),
                update: (event, ui) => this.handleDnDUpdate(event, ui)
            });
            this.appendChild(this.occurrenceViewsContainer);

            this.formItemSetOccurrences =
            new FormItemSetOccurrences(this.getContext(), this.occurrenceViewsContainer, formItemSet, dataSets);
            this.formItemSetOccurrences.layout();
            this.formItemSetOccurrences.addListener(<api.form.FormItemOccurrencesListener>{
                onOccurrenceAdded: (occurrenceAdded: api.form.FormItemOccurrence<any>) => {
                    this.refresh();
                },
                onOccurrenceRemoved: (occurrenceRemoved: api.form.FormItemOccurrence<any>) => {
                    this.refresh();
                }
            });

            this.formItemSetOccurrences.getFormItemSetOccurrenceViews().forEach((formItemSetOccurrenceView: api.form.formitemset.FormItemSetOccurrenceView)=> {

                formItemSetOccurrenceView.onValidityChanged((event: support.ValidityChangedEvent) => {

                    console.log("FormView.formItemView[" + event.getOrigin().toString() + "].onValidityChanged -> " + event.isValid());

                    var previousValidState = this.previousValidationRecording.isValid();
                    if(event.isValid() ){
                        this.previousValidationRecording.removeByPath(event.getOrigin());
                    }

                    if (previousValidState != this.previousValidationRecording.isValid()) {
                        this.notifyValidityChanged(new support.ValidityChangedEvent(this.previousValidationRecording, this.formItemSet.getPath()));
                    }
                });
            })
            this.bottomButtonRow = new api.dom.DivEl("bottom-button-row");
            this.appendChild(this.bottomButtonRow);

            this.addButton = new api.ui.Button("Add " + this.formItemSet.getLabel());
            this.addButton.setClass("add-button");
            this.addButton.setClickListener(() => {
                this.formItemSetOccurrences.createAndAddOccurrence();
                if (this.formItemSetOccurrences.isCollapsed()) {
                    this.collapseButton.getHTMLElement().click();
                }

            });
            this.collapseButton = new api.dom.AEl("collapse-button");
            this.collapseButton.setText("Collapse");
            this.collapseButton.setClickListener(() => {
                if (this.formItemSetOccurrences.isCollapsed()) {
                    this.collapseButton.setText("Collapse");
                    this.formItemSetOccurrences.toggleOccurences(true);
                } else {
                    this.collapseButton.setText("Expand");
                    this.formItemSetOccurrences.toggleOccurences(false);
                }

            });

            this.bottomButtonRow.appendChild(this.addButton);
            this.bottomButtonRow.appendChild(this.collapseButton);
            this.refresh();

            this.onValidityChanged((event: support.ValidityChangedEvent) => {

                if (event.isValid()) {
                    this.removeClass("invalid")
                }
                else {
                    this.addClass("invalid");
                }
            })
        }

        refresh() {

            this.addButton.setVisible(!this.formItemSetOccurrences.maximumOccurrencesReached());
        }

        public getFormItemSetOccurrenceView(index: number): FormItemSetOccurrenceView {
            return this.formItemSetOccurrences.getFormItemSetOccurrenceView(index);
        }

        getData(): api.data.Data[] {
            return this.getDataSets();
        }

        getDataSets(): api.data.DataSet[] {

            return this.formItemSetOccurrences.getDataSets();
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return this.formItemSetOccurrences.getAttachments();
        }

        hasValidOccurrences(): boolean {

            return this.getData().length >= this.formItemSet.getOccurrences().getMaximum();
        }

        validate(silent: boolean = true): ValidationRecorder {

            var recording = new ValidationRecorder();
            var occurrenceViews = this.formItemSetOccurrences.getFormItemSetOccurrenceViews();

            var numberOfValids = 0;
            occurrenceViews.forEach((occurrenceView: FormItemSetOccurrenceView) => {
                var recorderForOccurrence = occurrenceView.validate(silent);
                if (recorderForOccurrence.isValid()) {
                    numberOfValids++;
                }
                recording.flatten(recorderForOccurrence);
            });

            if (numberOfValids < this.formItemSet.getOccurrences().getMinimum()) {
                recording.breaksMinimumOccurrences(this.formItemSet.getPath());
            }
            if (this.formItemSet.getOccurrences().maximumBreached(numberOfValids)) {
                recording.breaksMaximumOccurrences(this.formItemSet.getPath());
            }

            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new support.ValidityChangedEvent(recording, this.formItemSet.getPath()));
                }
            }

            if (recording.isValid()) {
                this.removeClass("invalid")
            }
            else {
                this.addClass("invalid");
            }

            console.log("FormItemSetView[" + this.formItemSet.getPath().toString() + "].validate: " + recording.isValid());
            recording.print();

            this.previousValidationRecording = recording;
            return recording;
        }

        private addListener(eventName: support.InputTypeEvents, listener: (event: support.InputTypeEvent)=>void) {
            this.listeners[eventName].push(listener);
        }

        onValidityChanged(listener: (event: support.ValidityChangedEvent)=>void) {
            this.addListener(support.InputTypeEvents.ValidityChanged, listener);
        }

        private removeListener(eventName: support.InputTypeEvents, listener: (event: support.InputTypeEvent)=>void) {
            this.listeners[eventName].filter((currentListener: (event: support.InputTypeEvent)=>void) => {
                return listener == currentListener;
            });
        }

        unValidityChanged(listener: (event: support.ValidityChangedEvent)=>void) {
            this.removeListener(support.InputTypeEvents.ValidityChanged, listener);
        }

        private notifyListeners(eventName: support.InputTypeEvents, event: support.InputTypeEvent) {
            this.listeners[eventName].forEach((listener: (event: support.InputTypeEvent)=>void) => {
                listener(event);
            });
        }

        private notifyValidityChanged(event: support.ValidityChangedEvent) {
            this.notifyListeners(support.InputTypeEvents.ValidityChanged, event);
        }

        giveFocus(): boolean {

            var focusGiven = false;
            if (this.formItemSetOccurrences.getOccurrenceViews().length > 0) {
                var views: api.form.FormItemOccurrenceView[] = this.formItemSetOccurrences.getOccurrenceViews();
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

        private handleDnDStart(event: JQueryEventObject, ui): void {
            ui.placeholder.html("Drop form item set here");
        }

        private handleDnDUpdate(event: JQueryEventObject, ui) {

            var occurrenceOrderAccordingToDOM = this.resolveOccurrencesInOrderAccordingToDOM();

            // Update index of each occurrence
            occurrenceOrderAccordingToDOM.forEach((occurrence: FormItemSetOccurrence, index: number) => {
                occurrence.setIndex(index);
            });

            this.formItemSetOccurrences.sortOccurrences((a: FormItemSetOccurrence, b: FormItemSetOccurrence) => {
                return a.getIndex() - b.getIndex();
            });
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
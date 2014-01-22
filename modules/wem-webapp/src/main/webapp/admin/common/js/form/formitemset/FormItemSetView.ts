module api.form.formitemset {

    export class FormItemSetView extends api.form.FormItemView {

        private formItemSet: api.form.FormItemSet;

        private dataSets: api.data.DataSet[];

        private occurrenceViewsContainer: api.dom.DivEl;

        private formItemSetOccurrences: FormItemSetOccurrences;

        private bottomButtonRow: api.dom.DivEl;

        private addButton: api.ui.Button;

        private collapseButton: api.ui.Button;

        constructor(context: api.form.FormContext, formItemSet: api.form.FormItemSet, dataSets?: api.data.DataSet[]) {
            super("form-item-set-view", context, formItemSet);

            this.formItemSet = formItemSet;
            this.dataSets = dataSets != null ? dataSets : [];

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
            this.collapseButton = new api.ui.Button("Collapse");
            this.collapseButton.setClass("collapse-button");
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

        validate(validationRecorder: api.form.ValidationRecorder) {

            // TODO:
        }

        giveFocus(): boolean {

            var focusGiven = false;
            if (this.formItemSetOccurrences.getOccurrenceViews().length > 0) {
                var views:api.form.FormItemOccurrenceView[] = this.formItemSetOccurrences.getOccurrenceViews();
                for (var i = 0; i < views.length; i++) {
                    if (views[i].giveFocus()) {
                        focusGiven = true;
                        break;
                    }
                }
            }
            return focusGiven;
        }

        private createDnDHelper():string {
            var div = new api.dom.DivEl();
            div.getEl()
                .setId("drag-helper")
                .addClass("form-item-set-drop-allowed" )
                .setHeight("48px" )
                .setWidth("48px" )
                .setPosition("absolute")
                .setZindex(400000);
            return div.toString();
        }

        private handleDnDStart(event:JQueryEventObject, ui): void {
            ui.placeholder.html("Drop form item here");
        }

        private handleDnDUpdate(event:JQueryEventObject, ui) {

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
            for (var i = 0 ; i < formItemSetViewChildren.length ; i++) {
                var child = <HTMLElement> formItemSetViewChildren[i];
                var occurrenceView = this.formItemSetOccurrences.getOccurrenceViewById( child.id );
                if (occurrenceView) {
                    occurrencesInOrderAccordingToDOM.push(<FormItemSetOccurrence> occurrenceView.getOccurrence());
                }
            }

            return occurrencesInOrderAccordingToDOM;
        }
    }
}
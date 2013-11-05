module api_form_formitemset {

    export class FormItemSetView extends api_form.FormItemView {

        private formItemSet:api_form.FormItemSet;

        private dataSets:api_data.DataSet[];

        private occurrenceViewsContainer:api_dom.DivEl;

        private formItemSetOccurrences:FormItemSetOccurrences;

        private bottomButtonRow:api_dom.DivEl;

        private addButton:api_ui.Button;

        private collapseButton:api_ui.Button;

        constructor(formItemSet:api_form.FormItemSet, dataSets?:api_data.DataSet[]) {
            super("FormItemSetView", "form-item-set-view", formItemSet);

            this.formItemSet = formItemSet;
            this.dataSets = dataSets != null ? dataSets : [];

            this.occurrenceViewsContainer = new api_dom.DivEl(null, "occurrence-views-container");
            this.appendChild(this.occurrenceViewsContainer);

            this.formItemSetOccurrences = new FormItemSetOccurrences(this.occurrenceViewsContainer, formItemSet, dataSets);
            this.formItemSetOccurrences.layout();
            this.formItemSetOccurrences.addListener(<api_form.FormItemOccurrencesListener>{
                onOccurrenceAdded: (occurrenceAdded:api_form.FormItemOccurrence) => {
                    this.refresh();
                },
                onOccurrenceRemoved: (occurrenceRemoved:api_form.FormItemOccurrence) => {
                    this.refresh();
                }
            });

            this.bottomButtonRow = new api_dom.DivEl(null, "bottom-button-row");
            this.appendChild(this.bottomButtonRow);

            this.addButton = new api_ui.Button("Add " + this.formItemSet.getLabel());
            this.addButton.setClass("add-button");
            this.addButton.setClickListener(() => {
                this.formItemSetOccurrences.createAndAddOccurrence();
                if (this.formItemSetOccurrences.isCollapsed()) {
                    this.collapseButton.getHTMLElement().click();
                }

            });
            this.collapseButton = new api_ui.Button("Collapse");
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

        getFormItemViews():api_form.FormItemView[] {
            var occurrenceViews = this.formItemSetOccurrences.getFormItemSetOccurrenceViews();
            var formItemViews:api_form.FormItemView[] = [];
            occurrenceViews.forEach((occurrenceView:api_form_formitemset.FormItemSetOccurrenceView) => {
                formItemViews = formItemViews.concat(occurrenceView.getFormItemViews());
            });
            return formItemViews;
        }

        refresh() {

            this.addButton.setVisible(!this.formItemSetOccurrences.maximumOccurrencesReached());
        }

        getData():api_data.Data[] {
            return this.getDataSets();
        }

        getDataSets():api_data.DataSet[] {

            return this.formItemSetOccurrences.getDataSets();
        }

        hasValidOccurrences():boolean {

            return this.getData().length >= this.formItemSet.getOccurrences().getMaximum();
        }

        validate(validationRecorder:api_form.ValidationRecorder) {

            // TODO:
        }
    }
}
module app_wizard_form_formitemset {

    export class FormItemSetView extends app_wizard_form.FormItemView {

        private formItemSet:api_schema_content_form.FormItemSet;

        private dataSets:api_data.DataSet[];

        private occurrenceViewsContainer:api_dom.DivEl;

        private formItemSetOccurrences:FormItemSetOccurrences;

        private bottomButtonRow:api_dom.DivEl;

        private addButton:api_ui.Button;

        private collapseButton:api_ui.Button;

        constructor(formItemSet:api_schema_content_form.FormItemSet, dataSets?:api_data.DataSet[]) {
            super("FormItemSetView", "form-item-set-view", formItemSet);

            this.formItemSet = formItemSet;
            this.dataSets = dataSets != null ? dataSets : [];

            this.occurrenceViewsContainer = new api_dom.DivEl(null, "occurrence-views-container");
            this.appendChild(this.occurrenceViewsContainer);

            this.formItemSetOccurrences = new FormItemSetOccurrences(this.occurrenceViewsContainer, formItemSet, dataSets);
            this.formItemSetOccurrences.layout();
            this.formItemSetOccurrences.addListener(<app_wizard_form.FormItemOccurrencesListener>{
                onOccurrenceAdded: (occurrenceAdded:app_wizard_form.FormItemOccurrence) => {
                    this.refresh();
                },
                onOccurrenceRemoved: (occurrenceRemoved:app_wizard_form.FormItemOccurrence) => {
                    this.refresh();
                }
            });

            this.bottomButtonRow = new api_dom.DivEl(null, "bottom-button-row");
            this.appendChild(this.bottomButtonRow);

            this.addButton = new api_ui.Button("Add " + this.formItemSet.getLabel());
            this.addButton.setClass("add-button");
            this.addButton.setClickListener(() => {
                this.formItemSetOccurrences.createAndAddOccurrence();
            });
            this.collapseButton = new api_ui.Button("Collapse");
            this.collapseButton.setClass("collapse-button");

            this.bottomButtonRow.appendChild(this.addButton);
            this.bottomButtonRow.appendChild(this.collapseButton);
            this.refresh();
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
    }
}
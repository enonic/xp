module app_wizard_form {

    export class FormItemSetView extends FormItemView {

        private formItemSet:api_schema_content_form.FormItemSet;

        private dataSets:api_data.DataSet[];

        private occurrenceViewsContainer:api_dom.DivEl;

        private bottomButtonRow:api_dom.DivEl;

        private addButton:api_ui.Button;

        private collapseButton:api_ui.Button;

        constructor(formItemSet:api_schema_content_form.FormItemSet, dataSets?:api_data.DataSet[]) {
            super("FormItemSetView", "form-item-set-view", formItemSet);

            this.formItemSet = formItemSet;
            this.dataSets = dataSets != null ? dataSets : [];
            this.doLayout();
        }

        getData():api_data.Data[] {
            return this.getDataSets();
        }

        getDataSets():api_data.DataSet[] {

            var dataSets:api_data.DataSet[] = [];
            return dataSets;
        }

        private doLayout() {

            this.occurrenceViewsContainer = new api_dom.DivEl(null, "occurrence-views-container");
            this.appendChild(this.occurrenceViewsContainer);

            if (this.dataSets.length == 0) {

                this.doLayoutOccurrencesWithoutData();
            }
            else {
                this.doLayoutOccurrencesWithData();
            }

            this.bottomButtonRow = new api_dom.DivEl(null, "bottom-button-row");
            this.appendChild(this.bottomButtonRow);

            this.addButton = new api_ui.Button("Add " + this.formItemSet.getLabel());
            this.collapseButton = new api_ui.Button("Collapse");
            this.bottomButtonRow.appendChild(this.addButton);
            this.bottomButtonRow.appendChild(this.collapseButton);
        }

        private doLayoutOccurrencesWithoutData() {

            var occurrences:api_schema_content_form.Occurrences = this.formItemSet.getOccurrences();
            if (this.formItemSet.getOccurrences().getMinimum() > 1) {
                for (var i = 0; i < occurrences.getMinimum(); i++) {
                    var occurrenceView = new FormItemSetOccurrenceView(this.formItemSet, i);
                    this.occurrenceViewsContainer.appendChild(occurrenceView);
                }
            }
            else {
                var occurrenceView = new FormItemSetOccurrenceView(this.formItemSet, 0);
                this.occurrenceViewsContainer.appendChild(occurrenceView);
            }
        }

        private doLayoutOccurrencesWithData() {

            var occurrences:api_schema_content_form.Occurrences = this.formItemSet.getOccurrences();

            var occurrenceCount = 0;
            // Add one occurrence for each DataSet
            this.dataSets.forEach((dataSet:api_data.DataSet) => {
                var occurrenceView = new FormItemSetOccurrenceView(this.formItemSet, occurrenceCount, dataSet);
                occurrenceCount++;
                this.occurrenceViewsContainer.appendChild(occurrenceView);
            });

            // Adding any remaining occurrences to fulfill minimum required occurrences
            if (occurrenceCount < occurrences.getMinimum()) {
                for (var i = occurrenceCount - 1; i < occurrences.getMinimum(); i++) {
                    var occurrenceView = new FormItemSetOccurrenceView(this.formItemSet, i);
                    this.occurrenceViewsContainer.appendChild(occurrenceView);
                }
            }
        }
    }
}
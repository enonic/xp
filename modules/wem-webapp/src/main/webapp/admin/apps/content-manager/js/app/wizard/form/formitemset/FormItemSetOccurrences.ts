module app_wizard_form_formitemset {

    /*
     * A kind of a controller, which adds/removes FormItemSetOccurrenceView-s
     */
    export class FormItemSetOccurrences extends app_wizard_form.FormItemOccurrences {

        private formItemSet:api_schema_content_form.FormItemSet;

        private dataSets:api_data.DataSet[];

        constructor(occurrenceViewContainer:api_dom.Element, formItemSet:api_schema_content_form.FormItemSet, dataSets:api_data.DataSet[]) {
            super(formItemSet, occurrenceViewContainer, formItemSet.getOccurrences());

            this.formItemSet = formItemSet;
            this.dataSets = dataSets;

            if (dataSets != null && dataSets.length > 0) {
                this.constructOccurrencesForData();
            }
            else {
                this.constructOccurrencesForNoData();
            }
        }

        getFormItemSet():api_schema_content_form.FormItemSet {
            return this.formItemSet;
        }

        getAllowedOccurrences():api_schema_content_form.Occurrences {
            return this.formItemSet.getOccurrences();
        }

        private constructOccurrencesForData() {
            this.dataSets.forEach((dataSet:api_data.DataSet, index:number) => {
                this.addOccurrence(new FormItemSetOccurrence(this, index));
            });

            if (this.countOccurrences() < this.formItemSet.getOccurrences().getMinimum()) {
                for (var index:number = this.countOccurrences();
                     index < this.formItemSet.getOccurrences().getMinimum(); index++) {
                    this.addOccurrence(new FormItemSetOccurrence(this, index));
                }
            }
        }

        createNewOccurrence(formItemOccurrences:app_wizard_form.FormItemOccurrences,
                            insertAtIndex:number):app_wizard_form.FormItemOccurrence {
            return new FormItemSetOccurrence(<FormItemSetOccurrences>formItemOccurrences, insertAtIndex)
        }

        createNewOccurrenceView(occurrence:FormItemSetOccurrence):FormItemSetOccurrenceView {

            var formItemSetOccurrences:FormItemSetOccurrences = this;
            var newOccurrenceView:FormItemSetOccurrenceView = new FormItemSetOccurrenceView(occurrence, this.formItemSet, null);
            newOccurrenceView.addListener(<app_wizard_form.FormItemOccurrenceViewListener>{
                onRemoveButtonClicked: (toBeRemoved:app_wizard_form.FormItemOccurrenceView, index:number) => {
                    formItemSetOccurrences.doRemoveOccurrence(toBeRemoved, index);
                }
            });
            return newOccurrenceView;
        }

        getDataSets():api_data.DataSet[] {
            var dataSets:api_data.DataSet[] = [];
            this.getOccurrenceViews().forEach( (occurrenceView:FormItemSetOccurrenceView) => {
                var dataSet = occurrenceView.getDataSet();
                dataSets.push(dataSet);
            });
            return dataSets;
        }
    }
}
module api_form_formitemset {

    /*
     * A kind of a controller, which adds/removes FormItemSetOccurrenceView-s
     */
    export class FormItemSetOccurrences extends api_form.FormItemOccurrences<FormItemSetOccurrenceView> {

        private formItemSet:api_form.FormItemSet;

        private dataSets:api_data.DataSet[];

        private occurrencesCollapsed:boolean = false;

        private formItemSetOccurrenceViews:api_form_formitemset.FormItemSetOccurrenceView[] = [];

        constructor(occurrenceViewContainer:api_dom.Element, formItemSet:api_form.FormItemSet, dataSets:api_data.DataSet[]) {
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

        getFormItemSet():api_form.FormItemSet {
            return this.formItemSet;
        }

        getAllowedOccurrences():api_form.Occurrences {
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

        createNewOccurrence(formItemOccurrences:api_form.FormItemOccurrences<FormItemSetOccurrenceView>,
                            insertAtIndex:number):api_form.FormItemOccurrence<FormItemSetOccurrenceView> {
            return new FormItemSetOccurrence(<FormItemSetOccurrences>formItemOccurrences, insertAtIndex)
        }

        createNewOccurrenceView(occurrence:FormItemSetOccurrence):FormItemSetOccurrenceView {

            var formItemSetOccurrences:FormItemSetOccurrences = this;
            var dataSet:api_data.DataSet = this.dataSets != null ? this.dataSets[occurrence.getIndex()] : null;
            var newOccurrenceView:FormItemSetOccurrenceView = new FormItemSetOccurrenceView(occurrence, this.formItemSet, dataSet);
            newOccurrenceView.addListener(<api_form.FormItemOccurrenceViewListener>{
                onRemoveButtonClicked: (toBeRemoved:FormItemSetOccurrenceView, index:number) => {
                    formItemSetOccurrences.doRemoveOccurrence(toBeRemoved, index);
                }
            });
            this.formItemSetOccurrenceViews.push(newOccurrenceView);
            return newOccurrenceView;
        }

        getFormItemSetOccurrenceView(index:number):FormItemSetOccurrenceView {
            return this.formItemSetOccurrenceViews[index];
        }

        getDataSets():api_data.DataSet[] {
            var dataSets:api_data.DataSet[] = [];
            this.getOccurrenceViews().forEach((occurrenceView:FormItemSetOccurrenceView) => {
                var dataSet = occurrenceView.getDataSet();
                dataSets.push(dataSet);
            });
            return dataSets;
        }

        toggleOccurences(show:boolean) {
            var views = <FormItemSetOccurrenceView[]>this.getOccurrenceViews();
            this.occurrencesCollapsed = !show;
            views.forEach((formItemSetOccurenceView:FormItemSetOccurrenceView) => {
                formItemSetOccurenceView.toggleContainer(show);
            });
        }

        isCollapsed():boolean {
            return this.occurrencesCollapsed;
        }
    }
}
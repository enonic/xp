module api.form.formitemset {

    export interface FormItemSetOccurrencesConfig {

        context: api.form.FormContext;

        occurrenceViewContainer: api.dom.Element;

        formItemSet: api.form.FormItemSet;

        parent: FormItemSetOccurrenceView;

        parentDataSet: api.data.DataSet;
    }

    /*
     * A kind of a controller, which adds/removes FormItemSetOccurrenceView-s
     */
    export class FormItemSetOccurrences extends api.form.FormItemOccurrences<FormItemSetOccurrenceView> {

        private context: api.form.FormContext;

        private formItemSet: api.form.FormItemSet;

        private parent: FormItemSetOccurrenceView;

        private parentDataSet: api.data.DataSet;

        private occurrencesCollapsed: boolean = false;

        constructor(config: FormItemSetOccurrencesConfig) {
            super(<FormItemOccurrencesConfig>{
                formItem: config.formItemSet,
                occurrenceViewContainer: config.occurrenceViewContainer,
                allowedOccurrences: config.formItemSet.getOccurrences()
            });
            this.context = config.context;
            this.formItemSet = config.formItemSet;
            this.parent = config.parent;
            this.parentDataSet = config.parentDataSet;

            var dataSetCount = this.parentDataSet.nameCount(this.formItemSet.getName());
            if (dataSetCount > 0) {
                this.constructOccurrencesForData();
            }
            else {
                this.constructOccurrencesForNoData();
            }
        }

        private getDataSets(): api.data.DataSet[] {
            return this.parentDataSet.getDataSetsByName(this.formItemSet.getName());
        }

        getFormItemSet(): api.form.FormItemSet {
            return this.formItemSet;
        }

        getAllowedOccurrences(): api.form.Occurrences {
            return this.formItemSet.getOccurrences();
        }

        constructOccurrencesForNoData() {

            var minimumOccurrences = this.getAllowedOccurrences().getMinimum();
            if (minimumOccurrences > 0) {

                for (var i = 0; i < minimumOccurrences; i++) {
                    this.addOccurrence(this.createNewOccurrence(this, i));
                }
            }
            else {
                if (this.context.getShowEmptyFormItemSetOccurrences()) {
                    this.addOccurrence(this.createNewOccurrence(this, 0));
                }
            }
        }

        private constructOccurrencesForData() {
            this.getDataSets().forEach((dataSet: api.data.DataSet, index: number) => {
                this.addOccurrence(new FormItemSetOccurrence(this, index));
            });

            if (this.countOccurrences() < this.formItemSet.getOccurrences().getMinimum()) {
                for (var index: number = this.countOccurrences();
                     index < this.formItemSet.getOccurrences().getMinimum(); index++) {
                    this.addOccurrence(new FormItemSetOccurrence(this, index));
                }
            }
        }

        createNewOccurrence(formItemOccurrences: api.form.FormItemOccurrences<FormItemSetOccurrenceView>,
                            insertAtIndex: number): api.form.FormItemOccurrence<FormItemSetOccurrenceView> {
            return new FormItemSetOccurrence(<FormItemSetOccurrences>formItemOccurrences, insertAtIndex)
        }

        createNewOccurrenceView(occurrence: FormItemSetOccurrence): FormItemSetOccurrenceView {

            var dataSets = this.getDataSets();
            var dataSet: api.data.DataSet = dataSets[occurrence.getIndex()];
            if (!dataSet) {
                dataSet = new api.data.DataSet(this.formItemSet.getName());
                this.parentDataSet.addData(dataSet);
                console.log("FormItemSetOccurrences.createNewOccurrenceView() added dataset to parentDataSet: " +
                            dataSet.getPath().toString());
            }
            var newOccurrenceView = new FormItemSetOccurrenceView(<FormItemSetOccurrenceViewConfig>{
                context: this.context,
                formItemSetOccurrence: occurrence,
                formItemSet: this.formItemSet,
                parent: this.parent,
                dataSet: dataSet
            });
            console.log("FormItemSetOccurrences.createNewOccurrenceView() created occurrence view: " +
                        newOccurrenceView.getDataSet().getPath().toString());
            newOccurrenceView.onRemoveButtonClicked((event: RemoveButtonClickedEvent<FormItemSetOccurrenceView>) => {
                this.doRemoveOccurrence(event.getView(), event.getIndex());
            });
            return newOccurrenceView;
        }

        getAttachments(): api.content.attachment.Attachment[] {
            var attachments: api.content.attachment.Attachment[] = [];
            this.getOccurrenceViews().forEach((occurrenceView: FormItemSetOccurrenceView) => {
                occurrenceView.getAttachments().forEach((attachment: api.content.attachment.Attachment) => {
                    attachments.push(attachment);
                });
            });
            return attachments;
        }

        showOccurences(show: boolean) {
            var views = <FormItemSetOccurrenceView[]>this.getOccurrenceViews();
            this.occurrencesCollapsed = !show;
            views.forEach((formItemSetOccurenceView: FormItemSetOccurrenceView) => {
                formItemSetOccurenceView.showContainer(show);
            });
        }

        isCollapsed(): boolean {
            return this.occurrencesCollapsed;
        }

        reorderOccurrences(occurrencesIndexes: number[]) {

            var occurrenceViews: FormItemSetOccurrenceView[] = this.getOccurrenceViews();
            console.log("FormItemSetOccurrences.swapOccurrences");
            occurrenceViews.forEach((view: FormItemSetOccurrenceView, index: number) => {
                console.log("view[" + index + "] :" + view.getDataPath().toString());
                console.log("view[" + index + "].dataSet: " + this.dataSetToString(view.getDataSet()))
            });

            var dataSets = this.getDataSets();
            var arrays: any[] = [];
            occurrencesIndexes.forEach((i: number) => {
                var view = occurrenceViews[i];
                var viewDataSet = view.getDataSet();
                var viewDataArray = viewDataSet.getDataArray();
                arrays[i] = viewDataArray;

                //viewDataSet.setArrayIndex(view.getOccurrence().getIndex());

            });

            arrays.forEach((array: api.data.Data[], index: number) => {
                dataSets[index].setData(array);
            });


            dataSets.forEach((dataSet: api.data.DataSet, index: number) => {
                console.log("dataSet[" + index + "] after change: " + this.dataSetToString(dataSet));
            });
        }

        dataSetToString(dataSet: api.data.DataSet): string {
            var s = "";
            s += dataSet.getPath() + ": " + dataSet.getProperty("myText").getString();
            return s;
        }
    }
}
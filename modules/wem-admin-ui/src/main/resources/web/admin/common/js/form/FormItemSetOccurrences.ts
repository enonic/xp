module api.form {

    export interface FormItemSetOccurrencesConfig {

        context: FormContext;

        occurrenceViewContainer: api.dom.Element;

        formItemSet: FormItemSet;

        parent: FormItemSetOccurrenceView;

        parentDataSet: api.data.DataSet;
    }

    /*
     * A kind of a controller, which adds/removes FormItemSetOccurrenceView-s
     */
    export class FormItemSetOccurrences extends FormItemOccurrences<FormItemSetOccurrenceView> {

        private context: FormContext;

        private formItemSet: FormItemSet;

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

        getFormItemSet(): FormItemSet {
            return this.formItemSet;
        }

        getAllowedOccurrences(): Occurrences {
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

        createNewOccurrence(formItemOccurrences: FormItemOccurrences<FormItemSetOccurrenceView>,
                            insertAtIndex: number): FormItemOccurrence<FormItemSetOccurrenceView> {
            return new FormItemSetOccurrence(<FormItemSetOccurrences>formItemOccurrences, insertAtIndex)
        }

        createNewOccurrenceView(occurrence: FormItemSetOccurrence): FormItemSetOccurrenceView {

            var dataSets = this.getDataSets();
            var dataSet: api.data.DataSet = dataSets[occurrence.getIndex()];
            if (!dataSet) {
                dataSet = new api.data.DataSet(this.formItemSet.getName());
                this.parentDataSet.addData(dataSet);

            }
            var newOccurrenceView = new FormItemSetOccurrenceView(<FormItemSetOccurrenceViewConfig>{
                context: this.context,
                formItemSetOccurrence: occurrence,
                formItemSet: this.formItemSet,
                parent: this.parent,
                dataSet: dataSet
            });

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

        moveOccurrence(index: number, destinationIndex: number) {

            super.moveOccurrence(index, destinationIndex);

            this.parentDataSet.moveData(index, destinationIndex);
        }
    }
}
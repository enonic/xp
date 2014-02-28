module api.form.formitemset {

    /*
     * A kind of a controller, which adds/removes FormItemSetOccurrenceView-s
     */
    export class FormItemSetOccurrences extends api.form.FormItemOccurrences<FormItemSetOccurrenceView> {

        private context: api.form.FormContext;

        private formItemSet: api.form.FormItemSet;

        private parent: FormItemSetOccurrenceView;

        private dataSets: api.data.DataSet[];

        private occurrencesCollapsed: boolean = false;

        private formItemSetOccurrenceViews: api.form.formitemset.FormItemSetOccurrenceView[] = [];

        constructor(context: api.form.FormContext, occurrenceViewContainer: api.dom.Element, formItemSet: api.form.FormItemSet,
                    parent: FormItemSetOccurrenceView, dataSets: api.data.DataSet[]) {
            super(formItemSet, occurrenceViewContainer, formItemSet.getOccurrences());
            this.context = context;
            this.formItemSet = formItemSet;
            this.parent = parent;
            this.dataSets = dataSets;

            if (dataSets != null && dataSets.length > 0) {
                this.constructOccurrencesForData();
            }
            else {
                this.constructOccurrencesForNoData();
            }
        }

        getFormItemSet(): api.form.FormItemSet {
            return this.formItemSet;
        }

        getAllowedOccurrences(): api.form.Occurrences {
            return this.formItemSet.getOccurrences();
        }

        private constructOccurrencesForData() {
            this.dataSets.forEach((dataSet: api.data.DataSet, index: number) => {
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

            var formItemSetOccurrences: FormItemSetOccurrences = this;
            var dataSet: api.data.DataSet = this.dataSets != null ? this.dataSets[occurrence.getIndex()] : null;
            var newOccurrenceView: FormItemSetOccurrenceView = new FormItemSetOccurrenceView(this.context, occurrence, this.formItemSet,
                this.parent, dataSet);
            newOccurrenceView.onRemoveButtonClicked((event: RemoveButtonClickedEvent<FormItemSetOccurrenceView>) => {
                formItemSetOccurrences.doRemoveOccurrence(event.getView(), event.getIndex());
            });
            this.formItemSetOccurrenceViews.push(newOccurrenceView);
            return newOccurrenceView;
        }

        getFormItemSetOccurrenceView(index: number): FormItemSetOccurrenceView {
            return this.formItemSetOccurrenceViews[index];
        }

        getFormItemSetOccurrenceViews(): FormItemSetOccurrenceView[] {
            return this.formItemSetOccurrenceViews;
        }

        getDataSets(): api.data.DataSet[] {
            var dataSets: api.data.DataSet[] = [];
            this.getOccurrenceViews().forEach((occurrenceView: FormItemSetOccurrenceView) => {
                var dataSet = occurrenceView.getDataSet();
                dataSets.push(dataSet);
            });
            return dataSets;
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

        toggleOccurences(show: boolean) {
            var views = <FormItemSetOccurrenceView[]>this.getOccurrenceViews();
            this.occurrencesCollapsed = !show;
            views.forEach((formItemSetOccurenceView: FormItemSetOccurrenceView) => {
                formItemSetOccurenceView.toggleContainer(show);
            });
        }

        isCollapsed(): boolean {
            return this.occurrencesCollapsed;
        }
    }
}
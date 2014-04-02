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

        private dataSets: api.data.DataSet[];

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

            this.dataSets = this.parentDataSet.getDataSetsByName(this.formItemSet.getName());

            if (this.dataSets && this.dataSets.length > 0) {
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

            var dataSet: api.data.DataSet = this.dataSets[occurrence.getIndex()];
            if( !dataSet ) {
                dataSet = new api.data.DataSet(this.formItemSet.getName());
                this.dataSets.push(dataSet);
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
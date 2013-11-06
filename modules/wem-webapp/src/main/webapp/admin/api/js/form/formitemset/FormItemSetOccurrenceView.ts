module api_form_formitemset {

    export class FormItemSetOccurrenceView extends api_form.FormItemOccurrenceView {

        private formItemSetOccurrence:FormItemSetOccurrence;

        private formItemSet:api_form.FormItemSet;

        private occurrenceCountEl:api_dom.SpanEl;

        private removeButton:api_dom.AEl;

        private constructedWithData:boolean;

        private dataSet:api_data.DataSet;

        private formItemViews:api_form.FormItemView[] = [];

        private formItemSetOccurrencesContainer:api_dom.DivEl;

        constructor(formItemSetOccurrence:FormItemSetOccurrence, formItemSet:api_form.FormItemSet,
                    dataSet:api_data.DataSet) {
            super("FormItemSetOccurrenceView", "form-item-set-occurrence-view", formItemSetOccurrence);
            this.formItemSetOccurrence = formItemSetOccurrence;
            this.formItemSet = formItemSet;
            this.constructedWithData = dataSet != null;
            this.dataSet = dataSet;
            this.doLayout();
            this.refresh();
        }

        private doLayout() {

            var label = new FormItemSetLabel(this.formItemSet);
            this.appendChild(label);

            this.removeButton = new api_dom.AEl(null, "remove-button");
            this.appendChild(this.removeButton);
            this.removeButton.setClickListener(() => {
                this.notifyRemoveButtonClicked();
            });

            this.occurrenceCountEl = new api_dom.SpanEl(null, "occurrence-count");
            this.occurrenceCountEl.getEl().setInnerHtml("#" + (this.getIndex() + 1));
            this.appendChild(this.occurrenceCountEl);

            this.formItemSetOccurrencesContainer = new api_dom.DivEl(null, "form-item-set-occurrences-container");
            this.appendChild(this.formItemSetOccurrencesContainer);


            this.formItemViews =  new api_form.FormItemLayer().
                setFormItems(this.formItemSet.getFormItems()).
                setParentElement(this.formItemSetOccurrencesContainer).
                layout(this.dataSet);
        }

        getFormItemViews():api_form.FormItemView[] {
            return this.formItemViews;
        }

        refresh() {

            this.occurrenceCountEl.setHtml("#" + (this.formItemSetOccurrence.getIndex() + 1));
            this.getEl().setData("dataId", this.formItemSetOccurrence.getDataId().toString());

            this.removeButton.setVisible(this.formItemSetOccurrence.showRemoveButton());
        }

        getDataSet():api_data.DataSet {

            var dataSet = new api_data.DataSet(this.formItemSet.getName());
            this.formItemViews.forEach((formItemView:api_form.FormItemView) => {
                formItemView.getData().forEach((data:api_data.Data) => {
                    dataSet.addData(data);
                });
            });
            return dataSet;
        }

        toggleContainer(show:boolean) {
            if (show) {
                this.formItemSetOccurrencesContainer.show();
            } else {
                this.formItemSetOccurrencesContainer.hide();
            }
        }
    }

}
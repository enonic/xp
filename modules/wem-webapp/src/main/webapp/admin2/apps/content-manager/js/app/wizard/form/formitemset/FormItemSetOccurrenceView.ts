module app_wizard_form_formitemset {

    export class FormItemSetOccurrenceView extends app_wizard_form.FormItemOccurrenceView {

        private formItemSetOccurrence:FormItemSetOccurrence;

        private formItemSet:api_schema_content_form.FormItemSet;

        private occurrenceCountEl:api_dom.SpanEl;

        private removeButton:api_ui.Button;

        private constructedWithData:boolean;

        private dataSet:api_data.DataSet;

        private formItemViews:app_wizard_form.FormItemView[] = [];

        constructor(formItemSetOccurrence:FormItemSetOccurrence, formItemSet:api_schema_content_form.FormItemSet,
                    dataSet?:api_data.DataSet) {
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

            this.removeButton = new api_ui.Button("X");
            this.removeButton.setClass("remove-button");
            this.appendChild(this.removeButton);
            this.removeButton.setClickListener( () => {
                this.notifyRemoveButtonClicked();
            });

            this.occurrenceCountEl = new api_dom.SpanEl(null, "occurrence-count");
            this.occurrenceCountEl.getEl().setInnerHtml("#" + (this.getIndex() + 1));
            this.appendChild(this.occurrenceCountEl);

            var formItemSetOccurrencesContainer = new api_dom.DivEl(null, "form-item-set-occurrences-container");
            this.appendChild(formItemSetOccurrencesContainer);

            if (this.constructedWithData) {
                this.doLayoutWithData(formItemSetOccurrencesContainer);
            }
            else {
                this.doLayoutWithoutData(formItemSetOccurrencesContainer);
            }
        }

        private doLayoutWithoutData(parentEl:api_dom.DivEl) {
            this.formItemSet.getFormItems().forEach((formItem:api_schema_content_form.FormItem) => {

                if (formItem instanceof api_schema_content_form.FormItemSet) {
                    var formItemSet:api_schema_content_form.FormItemSet = <api_schema_content_form.FormItemSet>formItem;

                    console.log("FormItemSetOccurrenceView.doLayout() laying out FormItemSet: ", formItemSet);
                    var formItemSetView = new FormItemSetView(formItemSet);
                    parentEl.appendChild(formItemSetView);
                    this.formItemViews.push(formItemSetView);
                }
                else if (formItem instanceof api_schema_content_form.Input) {
                    var input:api_schema_content_form.Input = <api_schema_content_form.Input>formItem;

                    console.log("FormItemSetOccurrenceView.doLayout()  laying out Input: ", input);
                    var inputContainerView = new app_wizard_form_input.InputView(input);
                    parentEl.appendChild(inputContainerView);
                    this.formItemViews.push(inputContainerView);
                }
            });
        }

        private doLayoutWithData(parentEl:api_dom.DivEl) {

            this.formItemSet.getFormItems().forEach((formItem:api_schema_content_form.FormItem) => {

                if (formItem instanceof api_schema_content_form.FormItemSet) {
                    var formItemSet:api_schema_content_form.FormItemSet = <api_schema_content_form.FormItemSet>formItem;

                    console.log("FormItemSetOccurrenceView.doLayout() laying out FormItemSet: ", formItemSet);
                    var dataSets:api_data.DataSet[] = this.dataSet.getDataSetsByName(formItemSet.getName());

                    var formItemSetView = new FormItemSetView(formItemSet, dataSets);
                    parentEl.appendChild(formItemSetView);
                    this.formItemViews.push(formItemSetView);
                }
                else if (formItem instanceof api_schema_content_form.Input) {
                    var input:api_schema_content_form.Input = <api_schema_content_form.Input>formItem;

                    console.log("FormItemSetOccurrenceView.doLayout() laying out Input: ", input);
                    var properties:api_data.Property[] = this.dataSet.getPropertiesByName(input.getName());

                    var inputContainerView = new app_wizard_form_input.InputView(input, properties);
                    parentEl.appendChild(inputContainerView);
                    this.formItemViews.push(inputContainerView);
                }
            });
        }

        refresh() {

            this.occurrenceCountEl.setHtml("#" + (this.formItemSetOccurrence.getIndex() + 1));
            this.getEl().setData("dataId", this.formItemSetOccurrence.getDataId().toString());


            if (this.formItemSetOccurrence.showRemoveButton()) {
                this.removeButton.show();
            }
            else {
                this.removeButton.hide();
            }
        }
    }

}
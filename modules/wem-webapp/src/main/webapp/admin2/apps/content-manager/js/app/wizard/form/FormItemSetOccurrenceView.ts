module app_wizard_form {

    export class FormItemSetOccurrenceView extends api_dom.DivEl {

        private formItemSet:api_schema_content_form.FormItemSet;

        private index:number;

        private occurrenceCountEl:api_dom.SpanEl;

        private dataSet:api_data.DataSet;

        private formItemViews:FormItemView[] = [];

        constructor(formItemSet:api_schema_content_form.FormItemSet, index:number, dataSet?:api_data.DataSet) {
            super("FormItemSetOccurrenceView", "form-item-set-occurrence-view");

            this.formItemSet = formItemSet;
            this.index = index;
            this.dataSet = dataSet;
            this.doLayout();
        }

        getData():api_data.Data {
            return this.dataSet;
        }

        private doLayout() {

            var label = new FormItemSetLabel(this.formItemSet);
            this.appendChild(label);

            this.occurrenceCountEl = new api_dom.SpanEl(null, "occurrence-count");
            this.occurrenceCountEl.getEl().setInnerHtml("#" + (this.index + 1));
            this.appendChild(this.occurrenceCountEl);

            var wrappingDiv = new api_dom.DivEl(null, "form-item-set-container");
            this.appendChild(wrappingDiv);

            if (this.dataSet == null) {

                this.doLayoutWithoutData(wrappingDiv);
            }
            else {

                this.doLayoutWithData(wrappingDiv);
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
                    var inputContainerView = new app_wizard_form.InputContainerView(input);
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

                    var inputContainerView = new app_wizard_form.InputContainerView(input, properties);
                    parentEl.appendChild(inputContainerView);
                    this.formItemViews.push(inputContainerView);
                }
            });
        }
    }
}
module app_wizard_form {

    export class FormItemSetView extends FormItemView {

        private formItemSet:api_schema_content_form.FormItemSet;

        private dataSets:api_data.DataSet[];

        private formItemViews:FormItemView[] = [];

        constructor(formItemSet:api_schema_content_form.FormItemSet, dataSets?:api_data.DataSet[]) {
            super("FormItemSetView", "form-item-set-view", formItemSet);

            this.formItemSet = formItemSet;
            this.dataSets = dataSets != null ? dataSets : [];
            this.layout();
        }

        getData():api_data.Data[] {
            return this.getDataSets();
        }

        getDataSets():api_data.DataSet[] {

            var dataSets:api_data.DataSet[] = [];
            return dataSets;
        }

        private layout() {

            var label = new FormItemSetLabel(this.formItemSet);
            this.appendChild(label);

            var wrappingDiv = new api_dom.DivEl(null, "form-item-set-container");
            this.appendChild(wrappingDiv);

            if (this.dataSets.length == 0) {
                this.formItemSet.getFormItems().forEach((formItem:api_schema_content_form.FormItem) => {
                    if (formItem instanceof api_schema_content_form.FormItemSet) {
                        var formItemSet:api_schema_content_form.FormItemSet = <api_schema_content_form.FormItemSet>formItem;
                        console.log("FormItemSetView.layout() laying out FormItemSet: ", formItemSet);
                        var formItemSetView = new FormItemSetView(formItemSet);
                        wrappingDiv.appendChild(formItemSetView);
                        this.formItemViews.push(formItemSetView);
                    }
                    else if (formItem instanceof api_schema_content_form.Input) {
                        var input:api_schema_content_form.Input = <api_schema_content_form.Input>formItem;
                        console.log("FormItemSetView.layout()  laying out Input: ", input);
                        var inputContainerView = new app_wizard_form.InputContainerView(input);
                        wrappingDiv.appendChild(inputContainerView);
                        this.formItemViews.push(inputContainerView);
                    }
                });
            }
            else {
                this.dataSets.forEach( (dataSet:api_data.DataSet) => {
                    this.formItemSet.getFormItems().forEach( (formItem:api_schema_content_form.FormItem) => {
                        if (formItem instanceof api_schema_content_form.FormItemSet) {
                            var formItemSet:api_schema_content_form.FormItemSet = <api_schema_content_form.FormItemSet>formItem;
                            console.log("FormItemSetView.layout() laying out FormItemSet: ", formItemSet);
                            var dataSets:api_data.DataSet[] = dataSet.getDataSetsByName(formItemSet.getName());
                            var formItemSetView = new FormItemSetView(formItemSet, dataSets);
                            wrappingDiv.appendChild(formItemSetView);
                            this.formItemViews.push(formItemSetView);
                        }
                        else if (formItem instanceof api_schema_content_form.Input) {
                            var input:api_schema_content_form.Input = <api_schema_content_form.Input>formItem;
                            console.log("FormItemSetView.layout() laying out Input: ", input);
                            var properties:api_data.Property[] = dataSet.getPropertiesByName(input.getName());
                            var inputContainerView = new app_wizard_form.InputContainerView(input, properties);
                            wrappingDiv.appendChild(inputContainerView);
                            this.formItemViews.push(inputContainerView);
                        }
                    } );
                });
            }
        }
    }
}
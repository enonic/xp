module app_wizard_form {

    export class FormItemSetContainer extends FormItemContainer {

        private formItemSet:api_schema_content_form.FormItemSet;

        private parentDataSet:api_content_data.DataSet;

        private dataSets:api_content_data.DataSet[];

        constructor(formItemSet:api_schema_content_form.FormItemSet, parentDataSet:api_content_data.DataSet) {
            super(formItemSet);

            this.formItemSet = formItemSet;
            this.parentDataSet = parentDataSet;

            this.dataSets = this.parentDataSet.getDataSetsByName(formItemSet.getName());

            this.layout();
        }

        getData():api_content_data.Data[] {
            return this.getDataSets();
        }

        getDataSets():api_content_data.DataSet[] {

            var dataSets:api_content_data.DataSet[] = [];
            return dataSets;
        }

        private layout() {

            var label = new FormItemSetLabel(this.formItemSet);
            this.appendChild(label);

            this.dataSets.forEach( (dataSet:api_content_data.DataSet) => {
                var layer = new FormItemsLayer(this);
                layer.layout(this.formItemSet.getFormItems(), dataSet);
            } );
        }
    }
}
module api_form_layout {

    export class FieldSetView extends LayoutView {

        private fieldSet:api_form.FieldSet;

        private formItemViews:api_form.FormItemView[] = [];

         constructor(fieldSet:api_form.FieldSet, dataSet?:api_data.DataSet) {
            super(fieldSet, "FieldSetView", "field-set-view");

            this.fieldSet = fieldSet;
            this.doLayout(dataSet);
        }

        getData():api_data.Data[] {
            var dataArray:api_data.Data[] = [];
            this.formItemViews.forEach( (formItemView:api_form.FormItemView) => {
                dataArray = dataArray.concat(formItemView.getData());
            } );
            return dataArray;
        }

        getFormItemViews():api_form.FormItemView[] {
            return this.formItemViews;
        }

        private doLayout(dataSet:api_data.DataSet) {

            var label = new FieldSetLabel(this.fieldSet);
            this.appendChild(label);

            var wrappingDiv = new api_dom.DivEl(null, "field-set-container");
            this.appendChild(wrappingDiv);

            this.formItemViews =  new api_form.FormItemLayer().
                setFormItems(this.fieldSet.getFormItems()).
                setParentElement(wrappingDiv).
                layout(dataSet);
        }
    }
}
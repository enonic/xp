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

        public getFormItemView(name:string):api_form.FormItemView {

            // TODO: Performance could be improved if the views where accessible by name from a map

            for(var i = 0; i < this.formItemViews.length; i++) {
                var curr = this.formItemViews[i];
                if(name == curr.getFormItem().getName()) {
                    return curr;
                }
            }

            // FormItemView not found - look inside FieldSet-s
            for(var i = 0; i < this.formItemViews.length; i++) {
                var curr = this.formItemViews[i];
                if(curr instanceof FieldSetView) {
                    var view = (<FieldSetView>curr).getFormItemView( name );
                    if( view != null ) {
                        return view;
                    }
                }
            }

            return null;
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
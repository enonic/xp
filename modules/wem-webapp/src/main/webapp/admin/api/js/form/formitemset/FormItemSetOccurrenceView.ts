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

        public getValueAtPath(path:api_data.DataPath):api_data.Value {
            if( path == null ) {
                throw new Error("To get a value, a path is required");
            }
            if( path.elementCount() == 0 ) {
                throw new Error("Cannot get value from empty path: " + path.toString());
            }

            if( path.elementCount() == 1 ){
                return this.getValue(path.getFirstElement().toDataId());
            }
            else {
                return this.forwardGetValueAtPath(path);
            }
        }

        public getValue(dataId:api_data.DataId):api_data.Value{

            var inputView = this.getInputView( dataId.getName() );
            if( inputView == null ) {
                return null;
            }
            return inputView.getValue(dataId.getArrayIndex());
        }

        private forwardGetValueAtPath(path:api_data.DataPath):api_data.Value{

            var dataId:api_data.DataId = path.getFirstElement().toDataId();
            var formItemSetView = this.getFormItemSetView( dataId.getName() );
            if( formItemSetView == null ) {
                return null;
            }
            var formItemSetOccurrenceView = formItemSetView.getFormItemSetOccurrenceView(dataId.getArrayIndex());
            return formItemSetOccurrenceView.getValueAtPath(path.newWithoutFirstElement());
        }

        public getInputView(name:string):api_form_input.InputView {

            var formItemView = this.getFormItemView(name);
            if( formItemView == null ) {
                return null;
            }
            if( !(formItemView instanceof api_form_input.InputView) ) {
                throw new Error( "Found a FormItemView with name [" + name + "], but it was not an InputView" );
            }
            return <api_form_input.InputView>formItemView;
        }

        public getFormItemSetView(name:string):FormItemSetView {

            var formItemView = this.getFormItemView(name);
            if( formItemView == null ) {
                return null;
            }
            if( !(formItemView instanceof FormItemSetView) ) {
                throw new Error( "Found a FormItemView with name [" + name + "], but it was not an FormItemSetView" );
            }
            return <FormItemSetView>formItemView;
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
                if(curr instanceof api_form_layout.FieldSetView) {
                    var view = (<api_form_layout.FieldSetView>curr).getFormItemView( name );
                    if( view != null ) {
                        return view;
                    }
                }
            }

            return null;
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
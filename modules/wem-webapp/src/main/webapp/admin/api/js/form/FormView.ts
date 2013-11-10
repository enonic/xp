module api_form {

    export class FormView extends api_ui.Panel {

        private form:Form;

        private contentData:api_content.ContentData;

        private formItemViews:FormItemView[] = [];

        constructor(form:Form, contentData?:api_content.ContentData) {
            super("FormView");
            this.setClass("form-view");
            this.form = form;
            this.contentData = contentData;
            this.doLayout();
        }

        private doLayout() {
            this.formItemViews =  new FormItemLayer().
                setFormItems(this.form.getFormItems()).
                setParentElement(this).
                layout(this.contentData);
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

        public getFormItemSetView(name:string):api_form_formitemset.FormItemSetView {

            var formItemView = this.getFormItemView(name);
            if( formItemView == null ) {
                return null;
            }
            if( !(formItemView instanceof api_form_formitemset.FormItemSetView) ) {
                throw new Error( "Found a FormItemView with name [" + name + "], but it was not an FormItemSetView" );
            }
            return <api_form_formitemset.FormItemSetView>formItemView;
        }

        public getFormItemView(name:string):FormItemView {

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

        getContentData():api_content.ContentData {
            return this.contentData;
        }

        getAttachments(): api_content.Attachment[] {
            var attachments:api_content.Attachment[] = [];
            this.formItemViews.forEach( (formItemView:FormItemView) => {
                attachments = attachments.concat( formItemView.getAttachments() );
            });
            return attachments;
        }

        rebuildContentData():api_content.ContentData {
            var contentData:api_content.ContentData = new api_content.ContentData();
            this.formItemViews.forEach((formItemView:FormItemView) => {

                formItemView.getData().forEach((data:api_data.Data) => {
                    contentData.addData(data)
                });

            });
            return contentData;
        }
    }
}
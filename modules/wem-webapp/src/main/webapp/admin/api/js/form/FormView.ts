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


        getInputViewByPath(path:api_data.DataPath, formItemViews:FormItemView[] = this.formItemViews):api_form_input.InputView {
            // Loop through all formItemViews
            for (var i = 0; i<formItemViews.length; i++) {
                var currentFormItemView:FormItemView = formItemViews[i];
                // If name matches first path elements name
                console.log("CURRENT ITEM -> ", currentFormItemView.getFormItem().getName(), currentFormItemView ,"path->", path.getElement(0).getName());
                if (currentFormItemView.getFormItem().getName() == path.getElement(0).getName()) {
                    // If formItemView is InputView, end of path is reached
                    if (currentFormItemView instanceof api_form_input.InputView) {
                        if (path.getElements().length == 1) {
                            return <api_form_input.InputView>currentFormItemView;
                        } else {
                            //return null;
                            //TODO: maybe throw error
                            throw new Error('InputView must be last element of path: ' + path.toString());
                        }
                    } else {
                        if( path.getElements().length == 1 ) {
                            //return null;
                            //TODO: maybe throw error
                            throw new Error('Expected InputView to be last element of path. Got FormItemSetView: ' + path.toString());
                        }

                        if( currentFormItemView instanceof api_form_formitemset.FormItemSetView )
                        {
                            var formItemSetView = <api_form_formitemset.FormItemSetView>currentFormItemView;
                            return this.getInputViewByPath(path.newWithOutFirstElement(), formItemSetView.getFormItemViews());
                        }
                        else if( currentFormItemView instanceof api_form_layout.FieldSetView )
                        {
                            var fieldSetView = <api_form_layout.FieldSetView>currentFormItemView;
                            return this.getInputViewByPath(path.newWithOutFirstElement(), fieldSetView.getFormItemViews());
                        }
                        else {
                            throw new Error( "Unexpected FormItemView: " + currentFormItemView.getFormItem().toFormItemJson() );
                        }
                    }
                }
            }

            return null;
        }

        getContentData():api_content.ContentData {
            return this.contentData;
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
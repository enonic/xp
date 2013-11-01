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

        getInputViewByPath(path:api_data.DataPath, formItemViews:FormItemView[] = this.formItemViews):api_form_input.InputView {
            var inputView:api_form_input.InputView = null;
            console.log("input path->", path);
            // Loop through all formItemViews
            formItemViews.forEach((formItemView:FormItemView) => {
                console.log(formItemView.getFormItem().getName());
                // If name matches first path elements name
                console.log("first path element", path.getElement(0));
                if (formItemView.getFormItem().getName() == path.getElement(0).getName()) {
                    console.log("name matched!");
                    // If formItemView is InputView, end of path is reached
                    if (formItemView instanceof api_form_input.InputView) {
                        inputView = <api_form_input.InputView>formItemView;
                        console.log("set inputview", inputView);
                    } else {
                        var formItemSetView = <api_form_formitemset.FormItemSetView>formItemView;
                        console.log("calling on formItemSetView");
                        //console.log(path.newWithOutFirstElement(), formItemSetView.getFormItemViews());
                        this.getInputViewByPath(path.newWithOutFirstElement(), formItemSetView.getFormItemViews());
                    }

                }
            });

            return inputView;


//            var returnItem:api_form_input.InputView = null;
//            if (path.getElements().length == 1) {
//                this.formItemViews.forEach((formItem:FormItemView, index) => {
//                    console.log("form item views" + formItem.getFormItem().getName());
//                    if (formItem instanceof api_form_input.InputView) {
//                        if (formItem.getFormItem().getName() == path.getElement(0)) {
//                            returnItem = <api_form_input.InputView>formItem;
//                        }
//                    }
//                });
//            } else if (path.getElements().length > 1) {
//                this.formItemViews.forEach((formItem:FormItemView) => {
//                        if (formItem.getFormItem().getName() == path.getElement(0)) {
//                            if (formItem instanceof api_form_input.InputView) {
//                                throw new Error("Expected FormItemSetView")
//                            } else if (formItem instanceof api_form_formitemset.FormItemSetView) {
//                                var formItemSetView = <api_form_formitemset.FormItemSetView>formItem;
//                                formItemSetView.getInputViewByPath(path.newWithOutFirstElement());
//                            }
//                        }
//                    }
//                );
//            }

            //return returnItem;
        }

        private
            doLayout() {

            if (this.contentData == null) {
                this.contentData = new api_content.ContentData();

                this.form.getFormItems().forEach((formItem:FormItem) => {
                    if (formItem instanceof FieldSet) {
                        var fieldSet:FieldSet = <api_form.FieldSet>formItem;
                        console.log("FormView.doLayout() laying out FieldSet: ", fieldSet);
                        var fieldSetView = new api_form_layout.FieldSetView(fieldSet);
                        this.appendChild(fieldSetView);
                        this.formItemViews.push(fieldSetView);
                    }
                    else if (formItem instanceof FormItemSet) {
                        var formItemSet:FormItemSet = <FormItemSet>formItem;
                        console.log("FormView.doLayout() laying out FormItemSet: ", formItemSet);
                        var formItemSetView = new api_form_formitemset.FormItemSetView(formItemSet);
                        this.appendChild(formItemSetView);
                        this.formItemViews.push(formItemSetView);
                    }
                    else if (formItem instanceof Input) {
                        var input:Input = <Input>formItem;
                        console.log("FormView.doLayout() laying out Input: ", input);
                        var inputContainerView = new api_form_input.InputView(input);
                        this.appendChild(inputContainerView);
                        this.formItemViews.push(inputContainerView);
                    }
                });
            }
            else {
                this.form.getFormItems().forEach((formItem:FormItem) => {
                    if (formItem instanceof FormItemSet) {
                        var formItemSet:FormItemSet = <FormItemSet>formItem;
                        console.log("FormView.doLayout() laying out FormItemSet: ", formItemSet);
                        var dataSets:api_data.DataSet[] = this.contentData.getDataSetsByName(formItemSet.getName());
                        var formItemSetView = new api_form_formitemset.FormItemSetView(formItemSet, dataSets);
                        this.appendChild(formItemSetView);
                        this.formItemViews.push(formItemSetView);
                    }
                    else if (formItem instanceof Input) {
                        var input:Input = <Input>formItem;
                        console.log("FormView.doLayout() laying out Input: ", input);
                        var properties:api_data.Property[] = this.contentData.getPropertiesByName(input.getName());
                        var inputContainerView = new api_form_input.InputView(input, properties);
                        this.appendChild(inputContainerView);
                        this.formItemViews.push(inputContainerView);
                    }
                });
            }

        }

        getContentData():api_content.ContentData {
            return this.contentData;
        }

        rebuildContentData():api_content.ContentData {
            var contentData:api_content.ContentData = new api_content.ContentData();
            this.formItemViews.forEach((formItemContainer:FormItemView) => {

                formItemContainer.getData().forEach((data:api_data.Data) => {
                    contentData.addData(data)
                });

            });
            return contentData;
        }
    }
}
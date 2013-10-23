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
                        var dataSets:api_data.DataSet[] = this.contentData.getDataSetsByName(formItemSet.getName());
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
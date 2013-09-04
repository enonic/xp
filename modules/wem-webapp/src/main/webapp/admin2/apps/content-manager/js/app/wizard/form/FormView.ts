module app_wizard_form {

    export class FormView extends api_ui.Panel {

        private form:api_schema_content_form.Form;

        private contentData:api_content_data.ContentData;

        private formItemViews:FormItemView[] = [];

        constructor(form:api_schema_content_form.Form, contentData?:api_content_data.ContentData) {
            super("FormView");
            this.form = form;
            this.contentData = contentData;
            this.layout();
        }

        private layout() {

            if (this.contentData == null) {
                this.contentData = new api_content_data.ContentData();

                this.form.getFormItems().forEach((formItem:api_schema_content_form.FormItem) => {
                    if (formItem instanceof api_schema_content_form.FormItemSet) {
                        var formItemSet:api_schema_content_form.FormItemSet = <api_schema_content_form.FormItemSet>formItem;
                        console.log("FormView.layout() laying out FormItemSet: ", formItemSet);
                        var formItemSetView = new FormItemSetView(formItemSet);
                        this.appendChild(formItemSetView);
                        this.formItemViews.push(formItemSetView);
                    }
                    else if (formItem instanceof api_schema_content_form.Input) {
                        var input:api_schema_content_form.Input = <api_schema_content_form.Input>formItem;
                        console.log("FormView.layout() laying out Input: ", input);
                        var inputContainerView = new InputContainerView(input);
                        this.appendChild(inputContainerView);
                        this.formItemViews.push(inputContainerView);
                    }
                });
            }
            else {
                this.form.getFormItems().forEach((formItem:api_schema_content_form.FormItem) => {
                    if (formItem instanceof api_schema_content_form.FormItemSet) {
                        var formItemSet:api_schema_content_form.FormItemSet = <api_schema_content_form.FormItemSet>formItem;
                        console.log("FormView.layout() laying out FormItemSet: ", formItemSet);
                        var dataSets:api_data.DataSet[] = this.contentData.getDataSetsByName(formItemSet.getName());
                        var formItemSetView = new FormItemSetView(formItemSet, dataSets);
                        this.appendChild(formItemSetView);
                        this.formItemViews.push(formItemSetView);
                    }
                    else if (formItem instanceof api_schema_content_form.Input) {
                        var input:api_schema_content_form.Input = <api_schema_content_form.Input>formItem;
                        console.log("FormView.layout() laying out Input: ", input);
                        var properties:api_data.Property[] = this.contentData.getPropertiesByName(input.getName());
                        var inputContainerView = new InputContainerView(input, properties);
                        this.appendChild(inputContainerView);
                        this.formItemViews.push(inputContainerView);
                    }
                });
            }

        }

        getContentData():api_content_data.ContentData {
            return this.contentData;
        }

        rebuildContentData():api_content_data.ContentData {
            var contentData:api_content_data.ContentData = new api_content_data.ContentData();
            this.formItemViews.forEach((formItemContainer:FormItemView) => {

                formItemContainer.getData().forEach((data:api_data.Data) => {
                    contentData.addData(data)
                });

            });
            return contentData;
        }
    }
}
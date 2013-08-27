module app_wizard_form {

    export class FormCmp extends api_ui.Panel {

        private form:api_schema_content_form.Form;

        private contentData:api_content_data.ContentData;

        private formItemContainers:FormItemContainer[];

        constructor(form:api_schema_content_form.Form, contentData?:api_content_data.ContentData) {
            super("FormCmp");
            this.form = form;
            this.contentData = contentData;
            this.layout();
        }

        private layout() {

            var layer = new FormItemsLayer(this);
            if (this.contentData == null) {
                this.contentData = new api_content_data.ContentData();
            }

            this.formItemContainers = layer.layout(this.form.getFormItems(), this.contentData);
        }

        getContentData():api_content_data.ContentData {
            return this.contentData;
        }

        rebuildContentData():api_content_data.ContentData {
            var contentData:api_content_data.ContentData = new api_content_data.ContentData();
            this.formItemContainers.forEach((formItemContainer:FormItemContainer) => {

                formItemContainer.getData().forEach( (data:api_data.Data) => {
                    contentData.addData(data)
                });

            });
            return contentData;
        }
    }
}
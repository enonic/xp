module app_wizard {

    export class ContentForm extends api_ui_form.Form {

        private form:api_schema_content_form.Form;

        private formView:app_wizard_form.FormView;

        constructor(form:api_schema_content_form.Form) {
            super("ContentForm");

            this.form = form;
        }

        renderNew() {
            this.removeChildren();
            this.layout(null);
        }

        renderExisting(contentData:api_content_data.ContentData) {
            this.removeChildren();
            this.layout(contentData);
        }

        private layout(contentData?:api_content_data.ContentData) {

            this.formView = new app_wizard_form.FormView(this.form, contentData);
            this.appendChild(this.formView)
        }

        getContentData() {
            return this.formView.rebuildContentData();
        }
    }
}

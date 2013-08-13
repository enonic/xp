module app_wizard {

    export class ContentForm extends api_ui_form.Form {

        private form:api_schema_content_form.Form;

        private formCmp:app_wizard_form.FormCmp;

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

            this.formCmp = new app_wizard_form.FormCmp(this.form, contentData);
            this.appendChild(this.formCmp)
        }

        getContentData() {
            return this.formCmp.rebuildContentData();
        }
    }
}

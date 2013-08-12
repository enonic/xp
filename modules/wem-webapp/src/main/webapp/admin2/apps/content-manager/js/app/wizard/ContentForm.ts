module app_wizard {

    export class ContentForm extends api_ui.Form {

        private form:api_schema_content_form.Form;

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

            var formCmp = new app_wizard_form.FormCmp(this.form, contentData);
            this.appendChild(formCmp)
        }
    }
}

module app_wizard {

    export class ContentForm extends api_ui_form.Form {

        private form:api_form.Form;

        private formView:api_form.FormView;

        constructor(form:api_form.Form) {
            super("ContentForm");

            this.form = form;
        }

        renderNew() {
            this.removeChildren();
            this.layout(null);
        }

        renderExisting(contentData:api_content.ContentData) {
            this.removeChildren();
            this.layout(contentData);
        }

        private layout(contentData?:api_content.ContentData) {

            this.formView = new api_form.FormView(this.form, contentData);
            this.appendChild(this.formView)
        }

        getForm():api_form.Form {
            return this.form;
        }

        getContentData() {
            return this.formView.rebuildContentData();
        }
    }
}

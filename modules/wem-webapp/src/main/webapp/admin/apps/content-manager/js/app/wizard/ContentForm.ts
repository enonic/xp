module app_wizard {

    export class ContentForm extends api_ui_form.Form {

        private form:api_form.Form;

        private formView:api_form.FormView;

        constructor() {
            super("ContentForm");
        }

        renderNew(form:api_form.Form) {
            this.removeChildren();
            this.form = form;
            this.layout(form, null);
        }

        renderExisting(contentData:api_content.ContentData, form:api_form.Form) {
            this.removeChildren();
            this.form = form;
            this.layout(form, contentData);
        }

        private layout(form:api_form.Form, contentData?:api_content.ContentData) {

            this.formView = new api_form.FormView(form, contentData);
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

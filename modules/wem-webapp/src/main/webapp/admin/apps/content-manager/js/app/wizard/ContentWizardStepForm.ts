module app_wizard {

    export class ContentWizardStepForm extends api_app_wizard.WizardStepForm {

        private formContext: api_form.FormContext;

        private form: api_form.Form;

        private formView: api_form.FormView;

        constructor() {
            super("ContentWizardStepForm");
        }

        renderNew(formContext: api_form.FormContext, form: api_form.Form) {
            this.removeChildren();
            this.formContext = formContext;
            this.form = form;
            this.layout(form, null);
        }

        renderExisting(formContext: api_form.FormContext, contentData: api_content.ContentData, form: api_form.Form) {
            this.removeChildren();
            this.formContext = formContext;
            this.form = form;
            this.layout(form, contentData);
        }

        private layout(form: api_form.Form, contentData?: api_content.ContentData) {

            this.formView = new api_form.FormView(this.formContext, form, contentData);
            this.appendChild(this.formView)
        }

        getForm(): api_form.Form {
            return this.form;
        }

        getFormView(): api_form.FormView {
            return this.formView;
        }

        getContentData() {
            return this.formView.rebuildContentData();
        }

        giveFocus() {
            this.formView.giveFocus();
        }
    }
}

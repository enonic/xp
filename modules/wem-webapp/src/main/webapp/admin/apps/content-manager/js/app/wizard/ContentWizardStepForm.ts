module app.wizard {

    export class ContentWizardStepForm extends api.app.wizard.WizardStepForm {

        private formContext: api.form.FormContext;

        private form: api.form.Form;

        private formView: api.form.FormView;

        constructor() {
            super(true);
        }

        renderNew(formContext: api.form.FormContext, form: api.form.Form) {
            this.removeChildren();
            this.formContext = formContext;
            this.form = form;
            this.layout(form, null);
        }

        renderExisting(formContext: api.form.FormContext, contentData: api.content.ContentData, form: api.form.Form) {
            this.removeChildren();
            this.formContext = formContext;
            this.form = form;
            this.layout(form, contentData);
        }

        private layout(form: api.form.Form, contentData?: api.content.ContentData) {

            this.formView = new api.form.FormView(this.formContext, form, contentData);
            this.appendChild(this.formView)
        }

        getForm(): api.form.Form {
            return this.form;
        }

        getFormView(): api.form.FormView {
            return this.formView;
        }

        getContentData() {
            return this.formView.rebuildContentData();
        }

        giveFocus(): boolean  {
            return this.formView.giveFocus();
        }
    }
}

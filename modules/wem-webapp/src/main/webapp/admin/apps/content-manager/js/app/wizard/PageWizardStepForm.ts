module app_wizard {

    export class PageWizardStepForm extends api_app_wizard.WizardStepForm {

        private form:api_form.Form;

        private formView:api_form.FormView;

        constructor() {
            super("PageWizardStepForm");
        }

        renderNew(form:api_form.Form) {
            //this.removeChildren();
            //this.form = form;
            //this.layout(form, null);
        }

        renderExisting(content:api_content.Content, pageTemplate:api_content_page.PageTemplate, pageDescriptor:api_content_page.PageDescriptor) {
            //this.removeChildren();
            //this.form = form;
            //this.layout(form, contentData);
        }

        private layout(form:api_form.Form, contentData?:api_data.RootDataSet) {

            this.formView = new api_form.FormView(form, contentData);
            this.appendChild(this.formView)
        }

        getForm():api_form.Form {
            return this.form;
        }

        getFormView():api_form.FormView {
            return this.formView;
        }

        getContentData() {
            return this.formView.rebuildContentData();
        }
    }
}

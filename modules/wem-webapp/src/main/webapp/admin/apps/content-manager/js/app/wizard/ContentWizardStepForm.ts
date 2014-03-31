module app.wizard {

    export class ContentWizardStepForm extends api.app.wizard.WizardStepForm {

        private formContext: api.form.FormContext;

        private form: api.form.Form;

        private formView: api.form.FormView;

        private contentData: api.content.ContentData;

        private publishAction: api.ui.Action;

        constructor(publishAction: api.ui.Action) {
            super();
            this.publishAction = publishAction;
        }

        renderExisting(formContext: api.form.FormContext, contentData: api.content.ContentData, form: api.form.Form) {
            this.removeChildren();
            this.formContext = formContext;
            this.form = form;
            this.contentData = contentData;
            this.layout(form, contentData);
        }

        private layout(form: api.form.Form, contentData: api.content.ContentData) {

            this.formView = new api.form.FormView(this.formContext, form, contentData);
            this.formView.addEditContentRequestListener((content: api.content.ContentSummary) => {
                new app.browse.EditContentEvent([content]).fire();
            });

            this.appendChild(this.formView);

            this.publishAction.setEnabled(this.formView.isValid());
            this.formView.onValidityChanged((event: api.form.FormValidityChangedEvent) => {
                this.publishAction.setEnabled(event.isValid());
            });
        }

        getForm(): api.form.Form {
            return this.form;
        }

        getFormView(): api.form.FormView {
            return this.formView;
        }

        getContentData(): api.content.ContentData {

            return this.contentData;
        }

        giveFocus(): boolean {
            return this.formView.giveFocus();
        }
    }
}

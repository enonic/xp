module app.wizard {

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import Form = api.form.Form;
    import FormContext = api.form.FormContext;
    import FormView = api.form.FormView;
    import ContentData = api.content.ContentData;

    export class ContentWizardStepForm extends api.app.wizard.WizardStepForm {

        private formContext: FormContext;

        private form: Form;

        private formView: FormView;

        private contentData: ContentData;

        private publishAction: api.ui.Action;

        constructor(publishAction: api.ui.Action) {
            super();
            this.publishAction = publishAction;
        }

        renderExisting(formContext: FormContext, contentData: ContentData, form: Form) {

            this.formContext = formContext;
            this.form = form;
            this.contentData = contentData;
            this.layout(form, contentData);
        }

        private layout(form: Form, contentData: ContentData) {

            this.formView = new FormView(this.formContext, form, contentData);
            this.formView.setDoOffset(false);
            this.formView.onEditContentRequest((content: api.content.ContentSummary) => {
                new app.browse.EditContentEvent([content]).fire();
            });

            this.appendChild(this.formView);

            this.publishAction.setEnabled(this.formView.isValid());
            this.formView.onValidityChanged((event: api.form.FormValidityChangedEvent) => {
                this.publishAction.setEnabled(event.isValid());
            });
        }

        getForm(): Form {
            return this.form;
        }

        getFormView(): FormView {
            return this.formView;
        }

        getContentData(): ContentData {

            return this.contentData;
        }

        giveFocus(): boolean {
            return this.formView.giveFocus();
        }
    }
}

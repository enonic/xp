module app.wizard {

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import Form = api.form.Form;
    import FormContext = api.form.FormContext;
    import FormView = api.form.FormView;
    import ContentData = api.content.ContentData;

    export class ContentWizardStepForm extends BaseContentWizardStepForm {

        private formContext: FormContext;

        private form: Form;

        private formView: FormView;

        private contentData: ContentData;

        constructor() {
            super();
        }

        layout(formContext: FormContext, contentData: ContentData, form: Form) {

            this.formContext = formContext;
            this.form = form;
            this.contentData = contentData;
            this.doLayout(form, contentData);
            if (form.getFormItems().length === 0) {
                this.hide();
            }
        }

        private doLayout(form: Form, contentData: ContentData) {

            this.formView = new FormView(this.formContext, form, contentData);
            this.formView.setDoOffset(false);
            this.formView.onFocus((event) => {
                this.notifyFocused(event);
            });
            this.formView.onBlur((event) => {
                this.notifyBlurred(event);
            });
            this.formView.onEditContentRequest((content: api.content.ContentSummary) => {
                new app.browse.EditContentEvent([content]).fire();
            });

            this.appendChild(this.formView);

            this.formView.onValidityChanged((event: api.form.FormValidityChangedEvent) => {
                this.notifyValidityChanged(new WizardStepValidityChangedEvent(event.isValid()));
            });
            this.notifyValidityChanged(new WizardStepValidityChangedEvent(this.formView.isValid()));
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

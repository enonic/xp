module app.wizard {

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import Form = api.form.Form;
    import FormContext = api.form.FormContext;
    import FormView = api.form.FormView;
    import PropertyTree = api.data2.PropertyTree;
    import WizardStepValidityChangedEvent = api.app.wizard.WizardStepValidityChangedEvent;

    export class ContentWizardStepForm extends api.app.wizard.WizardStepForm {

        private formContext: FormContext;

        private form: Form;

        private formView: FormView;

        private data: PropertyTree;

        constructor() {
            super();
        }

        layout(formContext: FormContext, data: PropertyTree, form: Form) {

            this.formContext = formContext;
            this.form = form;
            this.data = data;
            this.doLayout(form, data);
            if (form.getFormItems().length === 0) {
                this.hide();
            }
        }

        private doLayout(form: Form, data: PropertyTree) {

            this.formView = new FormView(this.formContext, form, data.getRoot());
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

        public validate(silent?: boolean): api.form.ValidationRecording {
            return this.formView.validate(silent);
        }

        public displayValidationErrors(display: boolean) {
            this.formView.displayValidationErrors(display);
        }

        getForm(): Form {
            return this.form;
        }

        getFormView(): FormView {
            return this.formView;
        }

        getData(): PropertyTree {

            return this.data;
        }

        giveFocus(): boolean {
            return this.formView.giveFocus();
        }
    }
}

module app.wizard {

    import ConfirmationDialog = api.ui.dialog.ConfirmationDialog;
    import Form = api.form.Form;
    import FormContext = api.form.FormContext;
    import FormView = api.form.FormView;
    import PropertyTree = api.data.PropertyTree;
    import WizardStepValidityChangedEvent = api.app.wizard.WizardStepValidityChangedEvent;

    export class ContentWizardStepForm extends api.app.wizard.WizardStepForm {

        private formContext: FormContext;

        private form: Form;

        private formView: FormView;

        private data: PropertyTree;

        constructor() {
            super();
        }

        update(data: PropertyTree, unchangedOnly: boolean = true): wemQ.Promise<void> {
            this.data = data;
            return this.formView.update(data.getRoot(), unchangedOnly);
        }

        layout(formContext: FormContext, data: PropertyTree, form: Form): wemQ.Promise<void> {

            this.formContext = formContext;
            this.form = form;
            this.data = data;
            return this.doLayout(form, data).then(() => {
                if (form.getFormItems().length === 0) {
                    this.hide();
                }
            });
        }

        private doLayout(form: Form, data: PropertyTree): wemQ.Promise<void> {

            this.formView = new FormView(this.formContext, form, data.getRoot());
            return this.formView.layout().then(() => {

                this.formView.onFocus((event) => {
                    this.notifyFocused(event);
                });
                this.formView.onBlur((event) => {
                    this.notifyBlurred(event);
                });

                this.appendChild(this.formView);

                this.formView.onValidityChanged((event: api.form.FormValidityChangedEvent) => {
                    this.previousValidation = event.getRecording();
                    this.notifyValidityChanged(new WizardStepValidityChangedEvent(event.isValid()));
                });

                var formViewValid = this.formView.isValid();
                this.notifyValidityChanged(new WizardStepValidityChangedEvent(formViewValid));
            });
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

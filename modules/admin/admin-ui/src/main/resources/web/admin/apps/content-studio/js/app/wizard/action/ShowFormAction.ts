module app.wizard.action {

    export class ShowFormAction extends api.ui.Action {

        constructor(wizard: app.wizard.ContentWizardPanel) {
            super("Form");

            this.setEnabled(true);
            this.setTitle("Hide Page Editor");
            this.onExecuted(() => {
                wizard.showForm();
                new ShowContentFormEvent().fire();
            })
        }
    }

}

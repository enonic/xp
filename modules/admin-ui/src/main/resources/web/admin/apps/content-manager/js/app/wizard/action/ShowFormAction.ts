module app.wizard.action {

    export class ShowFormAction extends api.ui.Action {

        constructor(wizard: app.wizard.ContentWizardPanel) {
            super("Form");

            this.setEnabled(true);
            this.onExecuted(() => {
                new ShowContentFormEvent().fire();
                wizard.showForm();
            })
        }
    }

}

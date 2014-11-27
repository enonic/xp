module app.wizard.action {

    export class DeletePrincipalAction extends api.ui.Action {

        constructor(wizardPanel: api.app.wizard.WizardPanel<api.security.Principal>) {
            super("Delete", "mod+del", true);
            this.onExecuted(() => {
                api.ui.dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this user?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        wizardPanel.close();
                        // Delete user request
                    }).open();
            });
        }
    }

}

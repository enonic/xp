module app.wizard.action {

    export class DeleteUserItemAction extends api.ui.Action {

        constructor(wizardPanel: api.app.wizard.WizardPanel<api.Equitable>) {
            super("Delete", "mod+del", true);
            this.onExecuted(() => {
                api.ui.dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this item?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        wizardPanel.close();
                        // Delete user request
                    }).open();
            });
        }
    }

}

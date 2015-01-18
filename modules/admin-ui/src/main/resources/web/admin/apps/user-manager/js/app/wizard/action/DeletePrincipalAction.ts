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

                        var persistedItem = wizardPanel.getPersistedItem(),
                            isPrincipal = (wizardPanel instanceof PrincipalWizardPanel) && !!persistedItem;

                        if (isPrincipal) {
                            var principal = <api.security.Principal>persistedItem,
                                principalKey = principal.getKey();

                            new api.security.DeletePrincipalRequest()
                                .setKeys([principalKey])
                                .send()
                                .done((jsonResponse: api.rest.JsonResponse<any>) => {
                                    var json = jsonResponse.getJson();

                                    if (json.results && json.results.length > 0) {
                                        var key = json.results[0].principalKey;

                                        api.notify.showFeedback('Principal [' + key + '] deleted!');
                                        new api.security.UserItemDeletedEvent([principal]).fire();
                                    }
                                });
                        }
                    }).open();
            });
        }
    }

}

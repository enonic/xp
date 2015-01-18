module app.wizard.action {

    export class DeleteContentAction extends api.ui.Action {

        constructor(wizardPanel: api.app.wizard.WizardPanel<api.content.Content>) {
            super("Delete", "mod+del", true);
            this.onExecuted(() => {
                api.ui.dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this content?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        wizardPanel.close();
                        new api.content.DeleteContentRequest()
                            .addContentPath(wizardPanel.getPersistedItem().getPath())
                            .send()
                            .done((jsonResponse: api.rest.JsonResponse<any>) => {
                                var json = jsonResponse.getJson();

                                if (json.successes && json.successes.length > 0) {
                                    var path = json.successes[0].path;
                                    var deletedContent = wizardPanel.getPersistedItem();

                                    api.notify.showFeedback('Content [' + path + '] deleted!');
                                    new api.content.ContentDeletedEvent([deletedContent]).fire();
                                }
                            });
                    }).open();
            });
        }
    }

}

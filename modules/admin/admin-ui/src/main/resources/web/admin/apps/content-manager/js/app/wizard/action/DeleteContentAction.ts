module app.wizard.action {

    import ContentId = api.content.ContentId;

    export class DeleteContentAction extends api.ui.Action {

        constructor(wizardPanel: api.app.wizard.WizardPanel<api.content.Content>) {
            super("Delete", "mod+del", true);
            this.onExecuted(() => {
                api.ui.dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this content?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        new api.content.DeleteContentRequest()
                            .addContentPath(wizardPanel.getPersistedItem().getPath())
                            .sendAndParse()
                            .then((result: api.content.DeleteContentResult) => {
                                app.view.DeleteAction.showDeleteResult(result);
                                result.getDeleted().forEach((deleted) => {
                                    new api.content.ContentDeletedEvent(new ContentId(deleted.getId())).fire();
                                });
                                result.getPendings().forEach((pending) => {
                                    new api.content.ContentDeletedEvent(new ContentId(pending.getId()), true).fire();
                                });
                            }).catch((reason: any) => {
                                if (reason && reason.message) {
                                    api.notify.showError(reason.message);
                                } else {
                                    api.notify.showError('Content could not be deleted.');
                                }
                            }).done();
                    }).open();
            });
        }
    }

}

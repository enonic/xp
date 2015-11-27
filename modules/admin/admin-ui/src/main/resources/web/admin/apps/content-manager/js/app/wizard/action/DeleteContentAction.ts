module app.wizard.action {

    import ContentId = api.content.ContentId;
    import ContentPath = api.content.ContentPath;

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
                                var contentDeletedEvent = new api.content.ContentDeletedEvent();
                                app.view.DeleteAction.showDeleteResult(result);
                                result.getDeleted().forEach((deleted) => {
                                    contentDeletedEvent.addItem(new ContentId(deleted.getId()), ContentPath.fromString(deleted.getPath()));
                                });
                                result.getPendings().forEach((pending) => {
                                    contentDeletedEvent.addPendingItem(new ContentId(pending.getId()),
                                        ContentPath.fromString(pending.getPath()));
                                });
                                contentDeletedEvent.fireIfNotEmpty();
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

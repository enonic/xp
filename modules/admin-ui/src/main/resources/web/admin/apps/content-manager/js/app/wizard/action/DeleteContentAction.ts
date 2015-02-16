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
                            .sendAndParse()
                            .then((result: api.content.DeleteContentResult) => {
                                if (result.getDeleted().length > 0) {
                                    var path = result.getDeleted()[0].toString();
                                    api.notify.showFeedback('Content [' + path + '] deleted!');
                                } else {
                                    var reason = result.getDeleteFailures().length > 0 ? result.getDeleteFailures()[0].getReason() : '';
                                    api.notify.showWarning('Content could not be deleted. ' + reason);
                                }
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

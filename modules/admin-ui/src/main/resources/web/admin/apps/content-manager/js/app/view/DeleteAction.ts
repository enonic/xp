module app.view {

    export class DeleteAction extends api.ui.Action {

        constructor(itemViewPanel: api.app.view.ItemViewPanel<api.content.ContentSummary>) {
            super("Delete", "mod+del");

            this.onExecuted(() => {

                var contentToDelete = itemViewPanel.getItem().getModel();

                api.ui.dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this content?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        itemViewPanel.close();
                        new api.content.DeleteContentRequest()
                            .addContentPath(contentToDelete.getPath())
                            .sendAndParse()
                            .then((result: api.content.DeleteContentResult) => {
                                if (result.getDeleted().length > 0) {
                                    var path = result.getDeleted()[0].toString();
                                    api.notify.showFeedback('Content [' + path + '] deleted!');

                                    new api.content.ContentDeletedEvent([contentToDelete]).fire();
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

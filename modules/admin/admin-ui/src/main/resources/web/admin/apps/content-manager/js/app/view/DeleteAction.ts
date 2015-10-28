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
                                DeleteAction.showDeleteResult(result);
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

        public static showDeleteResult(result: api.content.DeleteContentResult) {
            if (result.getPendings().length == 1) {
                api.notify.showFeedback('\"' + result.getPendings()[0].getName() + '\" marked for deletion');
            } else if (result.getPendings().length > 1) {
                api.notify.showFeedback(result.getPendings().length + ' items marked for deletion');
            }

            if (result.getDeleted().length == 1) {
                api.notify.showFeedback('\"' + result.getDeleted()[0].getName() + '\" deleted');
            } else if (result.getDeleted().length > 1) {
                api.notify.showFeedback(result.getDeleted().length + ' items deleted');
            }

            if (result.getDeleteFailures().length > 0) {
                api.notify.showWarning('Content could not be deleted. ' + result.getDeleteFailures()[0].getReason());
            }
        }

    }
}

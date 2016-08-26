import "../../api.ts";

export class DeleteAction extends api.ui.Action {

    constructor(itemViewPanel: api.app.view.ItemViewPanel<api.content.ContentSummaryAndCompareStatus>) {
        super("Delete", "mod+del");

        this.onExecuted(() => {

            var contentToDelete = itemViewPanel.getItem().getModel().getContentSummary();

            api.ui.dialog.ConfirmationDialog.get()
                .setQuestion("Are you sure you want to delete this content?")
                .setNoCallback(null)
                .setYesCallback(() => {
                    itemViewPanel.close();
                    new api.content.resource.DeleteContentRequest()
                        .addContentPath(contentToDelete.getPath())
                        .sendAndParse()
                        .then((result: api.content.resource.result.DeleteContentResult) => {
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

    public static showDeleteResult(result: api.content.resource.result.DeleteContentResult) {

        if (result.getDeleted() > 0) {
            if (result.getDeleted() == 1) {
                api.notify.showSuccess(result.getDeleted() + ' item was deleted');
            } else {
                api.notify.showSuccess(result.getDeleted() + ' items were deleted');
            }
        }
        if (result.getPendings() > 0) {
            if (result.getPendings() == 1) {
                api.notify.showSuccess(result.getPendings() + ' item was marked for deletion');
            } else {
                api.notify.showSuccess(result.getPendings() + ' items were marked for deletion');
            }
        }

        if (result.getFailureReason()) {
            api.notify.showWarning(`Content could not be deleted. ${result.getFailureReason()}`);
        }
    }

}

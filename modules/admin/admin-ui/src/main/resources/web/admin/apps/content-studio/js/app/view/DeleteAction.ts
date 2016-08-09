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
        if(result.getPendings() + result.getDeleted() == 1) {
            if (result.getPendings() == 1) {
                api.notify.showFeedback(`"${result.getContentName()}" marked for deletion`);
            } else if (result.getPendings() > 1) {
                api.notify.showFeedback(`${result.getPendings()} items marked for deletion`);
            }

            else if (result.getDeleted() == 1) {
                let name = result.getContentName() ||
                           `Unnamed ${api.util.StringHelper.capitalizeAll(result.getContentType().replace(/-/g, " ").trim())}`;
                api.notify.showFeedback(name + " deleted");
            } else if (result.getDeleted() > 1) {
                api.notify.showFeedback(result.getDeleted() + ' items deleted');
            }


        } else {
            if (result.getDeleted() > 0) {
                api.notify.showSuccess(result.getDeleted() + ' items were deleted');
            }
            if (result.getPendings() > 0) {
                api.notify.showSuccess(result.getPendings() + ' items were marked for deletion');
            }
        }
        if (result.getFailureReason()) {
            api.notify.showWarning(`Content could not be deleted. ${result.getFailureReason()}`);
        }
    }

}

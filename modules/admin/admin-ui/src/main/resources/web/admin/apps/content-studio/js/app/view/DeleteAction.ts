import "../../api.ts";

export class DeleteAction extends api.ui.Action {

    constructor(itemViewPanel: api.app.view.ItemViewPanel<api.content.ContentSummaryAndCompareStatus>) {
        super('Delete', 'mod+del');

        this.onExecuted(() => {

            let contentToDelete = itemViewPanel.getItem().getModel().getContentSummary();

            api.ui.dialog.ConfirmationDialog.get()
                .setQuestion('Are you sure you want to delete this content?')
                .setNoCallback(null)
                .setYesCallback(() => {
                    itemViewPanel.close();
                    new api.content.resource.DeleteContentRequest()
                        .addContentPath(contentToDelete.getPath())
                        .sendAndParseWithPolling()
                        .then((message: string) => {
                            api.notify.showSuccess(message);
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

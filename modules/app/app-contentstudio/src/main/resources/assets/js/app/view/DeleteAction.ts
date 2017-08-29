import '../../api.ts';
import i18n = api.util.i18n;

export class DeleteAction extends api.ui.Action {

    constructor(itemViewPanel: api.app.view.ItemViewPanel<api.content.ContentSummaryAndCompareStatus>) {
        super(i18n('action.delete'), 'mod+del');

        let contentToDelete = itemViewPanel.getItem().getModel().getContentSummary();

        const confirmation = new api.ui.dialog.ConfirmationDialog()
            .setQuestion(i18n('dialog.confirm.delete'))
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
                        api.notify.showError(i18n('notify.content.deleteError');
                    }
                }).done();
            });

        this.onExecuted(() => {
            confirmation.open();
        });
    }
}

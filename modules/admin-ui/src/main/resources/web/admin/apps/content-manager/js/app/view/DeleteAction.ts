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
                            .send()
                            .done((jsonResponse: api.rest.JsonResponse<api.content.DeleteContentResult>) => {
                                var result = jsonResponse.getResult();
                                if (result.successes && result.successes.length > 0) {
                                    var path = result.successes[0].path;
                                    api.notify.showFeedback('Content [' + path + '] deleted!');

                                    new api.content.ContentDeletedEvent([contentToDelete]).fire();
                                }
                            });
                    }).open();
            });
        }

    }
}

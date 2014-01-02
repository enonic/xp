module app.view {

    export class EditContentAction extends api.ui.Action {

        constructor(panel:api.app.view.ItemViewPanel<api.content.ContentSummary>) {
            super("Edit");
            this.addExecutionListener(() => {
                new app.browse.EditContentEvent([panel.getItem().getModel()]).fire();
            });
        }
    }

    export class DeleteContentAction extends api.ui.Action {

        constructor(itemViewPanel:api.app.view.ItemViewPanel<api.content.ContentSummary>) {
            super("Delete", "mod+del");

            this.addExecutionListener(() => {

                var contentToDelete = itemViewPanel.getItem().getModel();

                api.ui.dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this content?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        itemViewPanel.close();
                        new api.content.DeleteContentRequest()
                            .addContentPath(contentToDelete.getPath())
                            .send()
                            .done((jsonResponse:api.rest.JsonResponse<api.content.DeleteContentResult>) => {
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

    export class CloseContentAction extends api.ui.Action {

        constructor(panel:api.ui.Panel, checkCanRemovePanel:boolean = true) {
            super("Close", "mod+f4");

            this.addExecutionListener(() => {
                new app.browse.CloseContentEvent(panel, checkCanRemovePanel).fire();
            });
        }
    }
}

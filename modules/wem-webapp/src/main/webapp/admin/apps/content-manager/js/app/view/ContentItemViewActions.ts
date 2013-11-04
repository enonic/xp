module app_view {

    export class EditContentAction extends api_ui.Action {

        constructor(panel:api_app_view.ItemViewPanel<api_content.ContentSummary>) {
            super("Edit");
            this.addExecutionListener(() => {
                new app_browse.EditContentEvent([panel.getItem().getModel()]).fire();
            });
        }
    }

    export class DeleteContentAction extends api_ui.Action {

        constructor(itemViewPanel:api_app_view.ItemViewPanel<api_content.ContentSummary>) {
            super("Delete", "mod+del");

            this.addExecutionListener(() => {

                var contentToDelete = itemViewPanel.getItem().getModel();

                api_ui_dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this content?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        itemViewPanel.close();
                        new api_content.DeleteContentRequest()
                            .addContentPath(contentToDelete.getPath())
                            .send()
                            .done((jsonResponse:api_rest.JsonResponse) => {
                                var json = jsonResponse.getJson();

                                if (json.successes && json.successes.length > 0) {
                                    var path = json.successes[0].path;
                                    api_notify.showFeedback('Content [' + path + '] deleted!');
                                    new api_content.ContentDeletedEvent([contentToDelete]).fire();
                                }
                                });
                    }).open();
            });
        }

    }

    export class CloseContentAction extends api_ui.Action {

        constructor(panel:api_ui.Panel, checkCanRemovePanel:boolean = true) {
            super("Close", "mod+f4");

            this.addExecutionListener(() => {
                new app_browse.CloseContentEvent(panel, checkCanRemovePanel).fire();
            });
        }
    }
}

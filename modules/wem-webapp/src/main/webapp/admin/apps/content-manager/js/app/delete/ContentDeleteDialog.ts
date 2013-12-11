module app_delete {

    export class ContentDeleteDialog extends api_app_delete.DeleteDialog {

        private contentToDelete:api_content.ContentSummary[];

        constructor() {
            super("Content");

            this.setDeleteAction(new ContentDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {

                var deleteRequest = new api_content.DeleteContentRequest();
                for (var i = 0; i < this.contentToDelete.length; i++) {
                    deleteRequest.addContentPath(this.contentToDelete[i].getPath());
                }

                deleteRequest.send().done((jsonResponse:api_rest.JsonResponse<api_content.DeleteContentResult>) => {

                    var result = jsonResponse.getResult(),
                        paths = [],
                        deletedContents:api_content.ContentSummary[] = [];

                    for (var i = 0; i < result.successes.length; i++) {
                        var path = result.successes[i].path;
                        paths.push(path);

                        this.contentToDelete.forEach((content:api_content.ContentSummary) => {
                            if(path == content.getPath().toString()) {
                                deletedContents.push(content);
                            }
                        })
                    }

                    this.close();

                    api_notify.showFeedback('Content [' + paths.join(', ') + '] deleted!');

                    new api_content.ContentDeletedEvent(deletedContents).fire();
                });
            });
        }

        setContentToDelete(contentModels:api_content.ContentSummary[]) {
            this.contentToDelete = contentModels;

            var deleteItems:api_app_delete.DeleteItem[] = [];
            for (var i in contentModels) {
                var content = contentModels[i];
                var deleteItem = new api_app_delete.DeleteItem(content.getIconUrl(), content.getDisplayName());
                deleteItems.push(deleteItem);
            }
            this.setDeleteItems(deleteItems);
        }
    }

    export class ContentDeleteDialogAction extends api_ui.Action {

        constructor() {
            super("Delete", "enter");
        }
    }
}
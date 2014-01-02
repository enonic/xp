module app.remove {

    export class ContentDeleteDialog extends api.app.remove.DeleteDialog {

        private contentToDelete:api.content.ContentSummary[];

        constructor() {
            super("Content");

            this.setDeleteAction(new ContentDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {

                var deleteRequest = new api.content.DeleteContentRequest();
                for (var i = 0; i < this.contentToDelete.length; i++) {
                    deleteRequest.addContentPath(this.contentToDelete[i].getPath());
                }

                deleteRequest.send().done((jsonResponse:api.rest.JsonResponse<api.content.DeleteContentResult>) => {

                    var result = jsonResponse.getResult(),
                        paths = [],
                        deletedContents:api.content.ContentSummary[] = [];

                    for (var i = 0; i < result.successes.length; i++) {
                        var path = result.successes[i].path;
                        paths.push(path);

                        this.contentToDelete.forEach((content:api.content.ContentSummary) => {
                            if(path == content.getPath().toString()) {
                                deletedContents.push(content);
                            }
                        })
                    }

                    this.close();

                    api.notify.showFeedback('Content [' + paths.join(', ') + '] deleted!');

                    new api.content.ContentDeletedEvent(deletedContents).fire();
                });
            });
        }

        setContentToDelete(contentModels:api.content.ContentSummary[]) {
            this.contentToDelete = contentModels;

            var deleteItems:api.app.remove.DeleteItem[] = [];
            for (var i in contentModels) {
                var content = contentModels[i];
                var deleteItem = new api.app.remove.DeleteItem(content.getIconUrl(), content.getDisplayName());
                deleteItems.push(deleteItem);
            }
            this.setDeleteItems(deleteItems);
        }
    }

    export class ContentDeleteDialogAction extends api.ui.Action {

        constructor() {
            super("Delete", "enter");
        }
    }
}
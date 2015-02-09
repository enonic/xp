module app.remove {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;

    export class ContentDeleteDialog extends api.app.remove.DeleteDialog {

        private contentToDelete: api.content.ContentSummary[];

        constructor() {
            super("Content");

            this.setDeleteAction(new ContentDeleteDialogAction());

            this.getDeleteAction().onExecuted(() => {

                var deleteRequest = new api.content.DeleteContentRequest();
                for (var i = 0; i < this.contentToDelete.length; i++) {
                    deleteRequest.addContentPath(this.contentToDelete[i].getPath());
                }

                deleteRequest.send().done((jsonResponse: api.rest.JsonResponse<api.content.DeleteContentResult>) => {

                    var result = jsonResponse.getResult(),
                        paths = [],
                        deletedContents: api.content.ContentSummary[] = [];

                    for (var i = 0; i < result.successes.length; i++) {
                        var path = result.successes[i].path;
                        paths.push(path);

                        this.contentToDelete.forEach((content: api.content.ContentSummary) => {
                            if (path == content.getPath().toString()) {
                                deletedContents.push(content);
                            }
                        })
                    }

                    this.close();

                    api.notify.showFeedback('Content [' + paths.join(', ') + '] deleted!');

                    new api.content.ContentDeletedEvent(deletedContents).fire();
                });
            });

            this.addCancelButtonToBottom();
        }

        setContentToDelete(contentModels: api.content.ContentSummary[]) {
            this.contentToDelete = contentModels;

            var deleteViewers: api.content.ContentSummaryViewer[] = [];
            for (var i in contentModels) {
                var content = contentModels[i];
                var deleteViewer = new api.content.ContentSummaryViewer();
                deleteViewer.setObject(content);
                deleteViewers.push(deleteViewer);
            }
            this.setDeleteViewers(deleteViewers);
        }
    }
}
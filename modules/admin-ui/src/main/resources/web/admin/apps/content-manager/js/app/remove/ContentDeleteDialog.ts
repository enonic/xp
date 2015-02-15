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

                deleteRequest.sendAndParse().then((result: api.content.DeleteContentResult) => {

                    var paths = [],
                        deletedContents: api.content.ContentSummary[] = [];

                    for (var i = 0; i < result.getDeleted().length; i++) {
                        var path = result.getDeleted()[i];
                        paths.push(path);

                        this.contentToDelete.forEach((content: api.content.ContentSummary) => {
                            if (content.getPath().equals(path)) {
                                deletedContents.push(content);
                            }
                        })
                    }

                    this.close();

                    if (result.getDeleted().length > 0) {
                        api.notify.showFeedback('Content [' + paths.map((path)=>path.toString()).join(', ') + '] deleted!');
                        new api.content.ContentDeletedEvent(deletedContents).fire();
                    } else {
                        var reason = result.getDeleteFailures().length > 0 ? result.getDeleteFailures()[0].getReason() : '';
                        api.notify.showWarning('Content could not be deleted. ' + reason);
                    }

                }).catch((reason: any) => {
                    if (reason && reason.message) {
                        api.notify.showError(reason.message);
                    } else {
                        api.notify.showError('Content could not be deleted.');
                    }
                }).done();
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
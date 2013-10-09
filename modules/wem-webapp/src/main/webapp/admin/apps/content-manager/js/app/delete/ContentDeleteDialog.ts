module app_delete {

    export class ContentDeleteDialog extends api_app_delete.DeleteDialog {

        private contentToDelete:api_content.ContentSummary[];

        private deleteHandler:api_handler.DeleteContentHandler = new api_handler.DeleteContentHandler();

        constructor() {
            super("Content");

            this.setDeleteAction(new ContentDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {

                var deleteRequest = new api_content.DeleteContentRequest();
                for ( var i = 0; i < this.contentToDelete.length; i++ ) {
                    deleteRequest.addContentPath(this.contentToDelete[i].getPath());
                }

                deleteRequest.send().done((jsonResponse:api_rest.JsonResponse) => {
                    var json = jsonResponse.getJson();

                    var paths = [];
                    for ( var i = 0; i < json.successes.length; i++ ) {
                        paths.push(json.successes[i].path);
                    }

                    this.close();
                    //components.gridPanel.refresh();

                    api_notify.showFeedback('Paths [' + paths.join(', ') + '] deleted!')
                });
            });
        }

        setContentToDelete(contentModels:api_content.ContentSummary[]) {
            this.contentToDelete = contentModels;

            var deleteItems:api_app_delete.DeleteItem[] = [];
            for (var i in contentModels) {
                var contentModel = contentModels[i];

                var deleteItem = new api_app_delete.DeleteItem(contentModel.getIconUrl(), contentModel.getDisplayName());
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
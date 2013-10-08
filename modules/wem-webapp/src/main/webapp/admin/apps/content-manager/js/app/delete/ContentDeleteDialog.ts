module app_delete {

    export class ContentDeleteDialog extends api_app_delete.DeleteDialog {

        private contentToDelete:api_content.ContentSummary[];

        private deleteHandler:api_handler.DeleteContentHandler = new api_handler.DeleteContentHandler();

        constructor() {
            super("Content");

            this.setDeleteAction(new ContentDeleteDialogAction());

            this.getDeleteAction().addExecutionListener(() => {
                this.deleteHandler.doDelete(api_handler.DeleteContentParamFactory.create(this.contentToDelete),
                    (result) => {
                        this.close();
                        //components.gridPanel.refresh();
                        api_notify.showFeedback('Content was deleted!')
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
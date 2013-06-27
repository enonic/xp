module app_delete {

    export class ContentDeleteDialog extends api_app_delete.DeleteDialog {

        private deleteAction:api_ui.Action = new ContentDeleteDialogAction();

        private contentToDelete:api_model.ContentModel[];

        private deleteHandler:api_handler.DeleteContentHandler = new api_handler.DeleteContentHandler();

        constructor() {
            super("Delete");

            this.setDeleteAction(this.deleteAction);

            var deleteCallback = (obj, success, result) => {
                this.close();
                //components.gridPanel.refresh();
                api_notify.showFeedback('Content was deleted!')
            }

            this.deleteAction.addExecutionListener(() => {
                this.deleteHandler.doDelete(api_handler.DeleteContentParamFactory.create(this.contentToDelete), deleteCallback);
            });

            document.body.appendChild(this.getHTMLElement());
        }

        setContentToDelete(contentModels:api_model.ContentModel[]) {
            this.contentToDelete = contentModels;

            var deleteItems:api_app_delete.DeleteItem[] = [];
            for (var i in contentModels) {
                var contentModel = contentModels[i];

                var deleteItem = new api_app_delete.DeleteItem(contentModel.data.iconUrl, contentModel.data.displayName);
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
module app {

    export class SpaceDeleteDialog extends api_app_delete.DeleteDialog {

        private deleteAction:api_ui.Action = new app.SpaceDeleteDialogAction();

        private spacesToDelete:api_model.SpaceExtModel[];

        private deleteHandler:api_handler.DeleteSpacesHandler = new api_handler.DeleteSpacesHandler();

        constructor() {
            super("Space");

            this.setDeleteAction(this.deleteAction);

            var deleteCallback = (obj, success, result) => {
                this.close();
                components.gridPanel.refresh();
                // TODO: fire DeletedEvent or give better feedback directly
                api_notify.showFeedback('Space(s) was deleted!');
            };

            this.deleteAction.addExecutionListener(() => {
                this.deleteHandler.doDelete(api_handler.DeleteSpaceParamFactory.create(this.spacesToDelete), deleteCallback);
            });
        }

        setSpacesToDelete(spaces:api_model.SpaceExtModel[]) {
            this.spacesToDelete = spaces;

            var deleteItems:api_app_delete.DeleteItem[] = [];
            for (var i in spaces) {
                var spaceModel = spaces[i];

                var deleteItem = new api_app_delete.DeleteItem(spaceModel.data.iconUrl, spaceModel.data.displayName);
                deleteItems.push(deleteItem);
            }
            this.setDeleteItems(deleteItems);
        }
    }

    export class SpaceDeleteDialogAction extends api_ui.Action {

        constructor() {
            super("Delete", "enter");
        }
    }
}

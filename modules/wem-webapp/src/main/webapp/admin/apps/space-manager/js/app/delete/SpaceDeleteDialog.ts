module app {

    export class SpaceDeleteDialog extends api_app_delete.DeleteDialog {


        private spacesToDelete:api_model.SpaceExtModel[];

        private deleteHandler:api_handler.DeleteSpacesHandler = new api_handler.DeleteSpacesHandler();

        constructor() {
            super("Space");
            var deleteAction = new app.SpaceDeleteDialogAction();
            this.setDeleteAction(deleteAction);

            deleteAction.addExecutionListener(() => {
                this.deleteHandler.doDelete(api_handler.DeleteSpaceParamFactory.create(this.spacesToDelete),
                    (result:api_remote_space.DeleteResult) => {
                        this.close();
                        components.gridPanel.refresh();
                        // TODO: fire DeletedEvent or give better feedback directly
                        api_notify.showFeedback('Space(s) was deleted!');
                    });
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

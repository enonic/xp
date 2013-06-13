module app_ui {

    /**
     * TODO: The upcoming successor of DeleteSpaceWindow, when the Toolbar code is working....
     */
    export class DeleteSpaceDialog extends api_delete.DeleteDialog {

        private deleteAction:api_ui.Action = new DeleteSpaceDialogAction();

        private spacesToDelete:api_model.SpaceModel[];

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

            this.deleteAction.addExecutionListener(()=> {
                this.deleteHandler.doDelete(api_handler.DeleteSpaceParamFactory.create( this.spacesToDelete ), deleteCallback );
            });

            document.body.appendChild(this.getHTMLElement());
        }

        setSpacesToDelete(spaces:api_model.SpaceModel[]) {
            this.spacesToDelete = spaces;

            var deleteItems:api_delete.DeleteItem[] = [];
            for (var i in spaces) {
                var space:api_model.SpaceModel = spaces[i];

                var deleteItem:api_delete.DeleteItem = new api_delete.DeleteItem(space.data.iconUrl, space.data.displayName);
                deleteItems.push(deleteItem);
            }
            this.setDeleteItems(deleteItems);
        }
    }

    export class DeleteSpaceDialogAction extends api_ui.Action {

        constructor() {
            super("Delete");
        }
    }


}

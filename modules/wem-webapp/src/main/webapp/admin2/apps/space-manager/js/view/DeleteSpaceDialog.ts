module app_ui {

    /**
     * TODO: The upcoming successor of DeleteSpaceWindow, when the Toolbar code is working....
     */
    export class DeleteSpaceDialog extends api_delete.DeleteDialog {

        private deleteAction:api_action.Action = new DeleteSpaceDialogAction();

        private spacesToDelete:app_model.SpaceModel[];

        private deleteHandler:api_handler.DeleteSpacesHandler = new api_handler.DeleteSpacesHandler();

        constructor() {
            super("Space");

            this.setDeleteAction(this.deleteAction);

            var deleteCallback = (obj, success, result) => {
                this.close();
                components.gridPanel.refresh();
                new app_event.DeletedEvent().fire();
            };

            this.deleteAction.addExecutionListener(()=> {
                this.deleteHandler.doDelete(app_handler.DeleteSpaceParamFactory.create( this.spacesToDelete ), deleteCallback );
            });

            document.body.appendChild(this.getHTMLElement());
        }

        setSpacesToDelete(spaces:app_model.SpaceModel[]) {
            this.spacesToDelete = spaces;

            var deleteItems:api_delete.DeleteItem[] = [];
            for (var i in spaces) {
                var space:app_model.SpaceModel = spaces[i];

                var deleteItem:api_delete.DeleteItem = new api_delete.DeleteItem(space.data.iconUrl, space.data.displayName);
                deleteItems.push(deleteItem);
            }
            this.setDeleteItems(deleteItems);
        }
    }

    export class DeleteSpaceDialogAction extends api_action.Action {

        constructor() {
            super("Delete");
        }
    }


}

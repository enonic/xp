module api_app_delete{

    export class DeleteDialog extends api_ui_dialog.ModalDialog {

        private modelName:string;

        private deleteAction:api_ui.Action;

        private cancelAction:api_ui.Action = new CancelDeleteDialogAction();

        private deleteItems:DeleteItem[];

        private itemList:DeleteDialogItemList = new DeleteDialogItemList();

        constructor(modelName:string) {
            super({
                title: "Delete " + modelName,
                width: 500,
                height: 300
            });

            this.modelName = modelName;

            this.getEl().addClass("delete-dialog");
            this.appendChildToContentPanel(this.itemList);
            this.setCancelAction(this.cancelAction);

            this.cancelAction.addExecutionListener(()=> {
                this.close();
            })
        }

        setDeleteAction(action:api_ui.Action) {
            this.deleteAction = action;
            this.addAction(action);
        }

        setDeleteItems(deleteItems:DeleteItem[]) {
            this.deleteItems = deleteItems;

            this.itemList.clear();

            if (deleteItems.length > 1) {
                this.setTitle("Delete " + this.modelName + "s");
            }
            else {
                this.setTitle("Delete " + this.modelName);
            }

            for (var i in this.deleteItems) {
                var deleteItem:DeleteItem = this.deleteItems[i];
                // TODO: created and add DeleteDialogItemList
                this.itemList.appendChild(new DeleteDialogItemComponent(deleteItem));
            }
        }
    }

    export class CancelDeleteDialogAction extends api_ui.Action {

        constructor() {
            super("Cancel", "esc");
        }
    }

    export class DeleteDialogItemList extends api_dom.DivEl {
        constructor() {
            super("DeleteDialogItemList");
            this.getEl().addClass("item-list");
        }

        clear() {
            this.removeChildren();
        }
    }

    class DeleteDialogItemComponent extends api_dom.DivEl {
        constructor(deleteItem:DeleteItem) {
            super("DeleteDialogItem");
            this.getEl().addClass("item");

            var icon = new api_dom.ImgEl(deleteItem.getIconUrl());
            this.appendChild(icon);

            var displayName = new api_dom.H4El();
            displayName.getEl().setInnerHtml(deleteItem.getDisplayName());
            this.appendChild(displayName);
        }
    }
}

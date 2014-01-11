module api.app.remove{

    export class DeleteDialog extends api.ui.dialog.ModalDialog {

        private modelName:string;

        private deleteAction:api.ui.Action;

        private deleteItems:DeleteItem[];

        private itemList:DeleteDialogItemList = new DeleteDialogItemList();

        constructor(modelName:string) {
            super({
                idPrefix: modelName + "DeleteDialog",
                title: "Delete " + modelName,
                width: 500,
                height: 300
            });
            this.setCancelAction(new CancelDeleteDialogAction());

            this.modelName = modelName;

            this.getEl().addClass("delete-dialog");
            this.appendChildToContentPanel(this.itemList);

            this.getCancelAction().addExecutionListener(()=> {
                this.close();
            })
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            super.close();
            this.remove();
        }

        setDeleteAction(action:api.ui.Action) {
            this.deleteAction = action;
            this.addAction(action);
        }

        getDeleteAction():api.ui.Action {
            return this.deleteAction;
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

    export class CancelDeleteDialogAction extends api.ui.Action {

        constructor() {
            super("Cancel", "esc");
        }
    }

    export class DeleteDialogItemList extends api.dom.DivEl {
        constructor() {
            super(true);
            this.getEl().addClass("item-list");
        }

        clear() {
            this.removeChildren();
        }
    }

    class DeleteDialogItemComponent extends api.dom.DivEl {
        constructor(deleteItem:DeleteItem) {
            super(true);
            this.getEl().addClass("item");

            var icon = new api.dom.ImgEl(deleteItem.getIconUrl());
            this.appendChild(icon);

            var displayName = new api.dom.H4El();
            displayName.getEl().setInnerHtml(deleteItem.getDisplayName());
            this.appendChild(displayName);
        }
    }
}

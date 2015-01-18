module api.app.remove {

    export class DeleteDialog extends api.ui.dialog.ModalDialog {

        private modelName: string;

        private deleteAction: api.ui.Action;

        private deleteViewers: api.ui.Viewer<any>[];

        private itemList: DeleteDialogItemList = new DeleteDialogItemList();

        constructor(modelName: string) {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Delete " + modelName)
            });

            this.modelName = modelName;

            this.getEl().addClass("delete-dialog");
            this.appendChildToContentPanel(this.itemList);

            this.setCancelAction(new CancelDeleteDialogAction());

            this.getCancelAction().onExecuted(()=> {
                this.close();
            });
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            super.close();
            this.remove();
        }

        setDeleteAction(action: api.ui.Action) {
            this.deleteAction = action;
            this.addAction(action, true, true);
        }

        getDeleteAction(): api.ui.Action {
            return this.deleteAction;
        }

        setDeleteViewers(deleteViewers: api.ui.Viewer<any>[]) {
            this.deleteViewers = deleteViewers;
            this.itemList.clear();

            if (deleteViewers.length > 1) {
                this.setTitle("Delete " + this.modelName + "s");
            }
            else {
                this.setTitle("Delete " + this.modelName);
            }

            for (var i in this.deleteViewers) {
                var deleteViewer: api.ui.Viewer<any> = this.deleteViewers[i];
                this.itemList.appendChild(deleteViewer);
            }
        }
    }


    export class DeleteDialogItemList extends api.dom.DivEl {
        constructor() {
            super();
            this.getEl().addClass("item-list");
        }

        clear() {
            this.removeChildren();
        }
    }
}

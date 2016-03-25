module api.app.remove {

    import SelectionItem = api.app.browse.SelectionItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import DialogButton = api.ui.dialog.DialogButton;

    export class DeleteDialog extends api.ui.dialog.ModalDialog {

        private modelName: string;

        private deleteAction: api.ui.Action;

        private itemList: DeleteDialogItemList = new DeleteDialogItemList();

        private subTitle: api.dom.Element;

        constructor(modelName: string) {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Delete " + modelName)
            });

            this.modelName = modelName;

            this.getEl().addClass("delete-dialog");
            this.appendChildToContentPanel(this.itemList);

            this.subTitle = new api.dom.H6El().addClass("desc-message").
                setHtml("Delete selected items and their children");
            this.appendChildToTitle(this.subTitle);
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            super.close();
            this.remove();
        }

        setDeleteAction(action: api.ui.Action): DialogButton {
            this.deleteAction = action;
            return this.addAction(action, true, true);
        }

        getDeleteAction(): api.ui.Action {
            return this.deleteAction;
        }

        renderSelectedItems(selectedItems: SelectionItem<ContentSummaryAndCompareStatus>[]) {
            this.itemList.clear();

            if (selectedItems.length > 1) {
                this.setTitle("Delete " + this.modelName + "s");
            }
            else {
                this.setTitle("Delete " + this.modelName);
            }

            for (var i in selectedItems) {
                var selectionItem: SelectionItem<ContentSummaryAndCompareStatus> = selectedItems[i];
                this.itemList.appendChild(selectionItem);
            }
        }

        updateSubTitleText(text: string) {
            this.subTitle.setHtml(text);
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

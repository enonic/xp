module api.app.remove {

    import SelectionItem = api.app.browse.SelectionItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import DialogButton = api.ui.dialog.DialogButton;
    import ListBox = api.ui.selector.list.ListBox;

    export class DeleteDialog<M> extends api.ui.dialog.ModalDialog {

        private modelName: string;

        private deleteAction: api.ui.Action;

        private itemList: ListBox<M>;

        private subTitle: api.dom.Element;

        constructor(modelName: string) {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Delete " + modelName)
            });
            this.getEl().addClass("delete-dialog");

            this.modelName = modelName;

            this.itemList = this.createItemList();
            this.itemList.addClass("item-list");
            this.appendChildToContentPanel(this.itemList);

            this.subTitle = new api.dom.H6El("desc-message")
                .setHtml("Delete selected items and their children");
            this.appendChildToTitle(this.subTitle);
        }

        protected createItemList(): ListBox<M> {
            throw new Error("createItemList() should be implemented");
        }

        protected getItemList(): ListBox<M> {
            return this.itemList;
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

        setListItems(items: M[]) {
            this.setTitle("Delete " + this.modelName + (items.length > 1 ? "s" : ''));
            this.itemList.setItems(items);
        }

        updateSubTitle(text: string) {
            this.subTitle.setHtml(text);
        }
    }

}

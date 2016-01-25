module app.remove {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import BrowseItem = api.app.browse.BrowseItem;
    import SelectionItem = api.app.browse.SelectionItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentPath = api.content.ContentPath;
    import DialogButton = api.ui.dialog.DialogButton;

    export class ContentDeleteDialog extends api.app.remove.DeleteDialog {

        private selectedItems: SelectionItem<ContentSummaryAndCompareStatus>[];

        private deleteButton: DialogButton;

        private yesCallback: () => void;

        private noCallback: () => void;

        constructor() {
            super("item");

            this.deleteButton = this.setDeleteAction(new ContentDeleteDialogAction());

            this.addDeleteActionHandler();

            this.addCancelButtonToBottom();
        }

        setContentToDelete(contents: ContentSummaryAndCompareStatus[]) {

            this.selectedItems = [];

            contents.forEach((content: ContentSummaryAndCompareStatus) => {
                this.selectedItems.push(this.createSelectionItemForDelete(content));
            });

            this.renderSelectedItems(this.selectedItems);

            this.countItemsToDeleteAndUpdateButtonCounter();
        }

        setYesCallback(callback: () => void) {
            this.yesCallback = callback;
        }

        setNoCallback(callback: () => void) {
            this.noCallback = callback;
        }

        private indexOf(item: SelectionItem<ContentSummaryAndCompareStatus>): number {
            for (var i = 0; i < this.selectedItems.length; i++) {
                if (item.getBrowseItem().getPath() == this.selectedItems[i].getBrowseItem().getPath()) {
                    return i;
                }
            }
            return -1;
        }

        private addDeleteActionHandler() {
            this.getDeleteAction().onExecuted(() => {

                this.yesCallback();
                this.deleteButton.setEnabled(false);
                this.showLoadingSpinner();

                this.createDeleteRequest().sendAndParse().then((result: api.content.DeleteContentResult) => {
                    this.close();
                    app.view.DeleteAction.showDeleteResult(result);
                }).catch((reason: any) => {
                    if (reason && reason.message) {
                        api.notify.showError(reason.message);
                    } else {
                        api.notify.showError('Content could not be deleted.');
                    }
                }).finally(() => {
                    this.deleteButton.setEnabled(true);
                    this.hideLoadingSpinner();
                }).done();
            });
        }

        private createSelectionItemForDelete(content: ContentSummaryAndCompareStatus): SelectionItem<ContentSummaryAndCompareStatus> {

            var deleteItemViewer = new api.content.ContentSummaryAndCompareStatusViewer();
            deleteItemViewer.setObject(content);

            var browseItem = new BrowseItem<ContentSummaryAndCompareStatus>(content).
                setId(content.getId()).
                setDisplayName(content.getDisplayName()).
                setPath(content.getPath().toString()).
                setIconUrl(new ContentIconUrlResolver().setContent(content.getContentSummary()).resolve());

            var selectionItem = new SelectionItem(deleteItemViewer, browseItem, () => {
                var index = this.indexOf(selectionItem);
                if (index < 0) {
                    return;
                }

                this.selectedItems[index].remove();
                this.selectedItems.splice(index, 1);

                if (this.selectedItems.length == 0) {
                    this.close();
                }
                else {
                    this.countItemsToDeleteAndUpdateButtonCounter();
                }
            });

            return selectionItem;
        }

        private countItemsToDeleteAndUpdateButtonCounter() {
            this.cleanDeleteButtonText();
            this.showLoadingSpinner();

            this.createRequestForCountingItemsToDelete().sendAndParse().then((itemsToDeleteCounter: number) => {
                this.hideLoadingSpinner();
                this.updateDeleteButtonCounter(itemsToDeleteCounter);
            }).finally(() => {
                this.hideLoadingSpinner();
            }).done();
        }


        private createRequestForCountingItemsToDelete(): api.content.CountContentsWithDescendantsRequest {
            var countContentChildrenRequest = new api.content.CountContentsWithDescendantsRequest();
            for (var j = 0; j < this.selectedItems.length; j++) {
                countContentChildrenRequest.addContentPath(ContentPath.fromString(this.selectedItems[j].getBrowseItem().getPath()));
            }

            return countContentChildrenRequest;
        }

        private createDeleteRequest(): api.content.DeleteContentRequest {
            var deleteRequest = new api.content.DeleteContentRequest();
            for (var i = 0; i < this.selectedItems.length; i++) {
                deleteRequest.addContentPath(ContentPath.fromString(this.selectedItems[i].getBrowseItem().getPath()));
            }

            return deleteRequest;
        }

        private updateDeleteButtonCounter(count: number) {
            this.deleteButton.setLabel("Delete (" + count + ")");
        }

        private showLoadingSpinner() {
            this.deleteButton.addClass("spinner");
        }

        private hideLoadingSpinner() {
            this.deleteButton.removeClass("spinner");
        }

        private cleanDeleteButtonText() {
            this.deleteButton.setLabel("Delete ");
        }
    }
}
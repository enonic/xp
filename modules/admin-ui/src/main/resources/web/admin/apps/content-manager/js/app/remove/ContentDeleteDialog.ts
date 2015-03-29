module app.remove {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import BrowseItem = api.app.browse.BrowseItem;
    import SelectionItem = api.app.browse.SelectionItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentPath = api.content.ContentPath;
    import DialogButton = api.ui.dialog.DialogButton;

    export class ContentDeleteDialog extends api.app.remove.DeleteDialog {

        private selectedItems: SelectionItem<ContentSummary>[];

        private deleteButton: DialogButton;

        constructor() {
            super("Content");

            this.deleteButton = this.setDeleteAction( new ContentDeleteDialogAction() );

            this.getDeleteAction().onExecuted(() => {

                this.createDeleteRequest().sendAndParse().then((result: api.content.DeleteContentResult) => {
                    this.close();
                    this.showDeleteResult(result);
                }).catch((reason: any) => {
                    if (reason && reason.message) {
                        api.notify.showError(reason.message);
                    } else {
                        api.notify.showError('Content could not be deleted.');
                    }
                }).done();
            });

            this.addCancelButtonToBottom();
        }

        setContentToDelete(contents: ContentSummary[]) {

            this.selectedItems = [];

            for (var i in contents) {
                this.selectedItems.push(this.createSelectionItemForDelete(contents[i]));
            }

            this.renderSelectedItems( this.selectedItems );

            this.countItemsToDeleteAndUpdateButtonCounter();
        }

        private indexOf(item: SelectionItem<ContentSummary>): number {
            for (var i = 0; i < this.selectedItems.length; i++) {
                if (item.getBrowseItem().getPath() == this.selectedItems[i].getBrowseItem().getPath()) {
                    return i;
                }
            }
            return -1;
        }

        private createSelectionItemForDelete(content: ContentSummary): SelectionItem<ContentSummary> {

            var deleteItemViewer = new api.content.ContentSummaryViewer();
            deleteItemViewer.setObject(content);

            var browseItem = new BrowseItem<ContentSummary>(content).
                setId(content.getId()).
                setDisplayName(content.getDisplayName()).
                setPath(content.getPath().toString()).
                setIconUrl(new ContentIconUrlResolver().setContent(content).resolve());

            var selectionItem = new SelectionItem(deleteItemViewer, browseItem, () => {
                var index = this.indexOf(selectionItem);
                if (index < 0) {
                    return;
                }

                this.selectedItems[index].remove();
                this.selectedItems.splice(index, 1);

                if(this.selectedItems.length == 0) {
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


        private createRequestForCountingItemsToDelete(): api.content.CountItemsWithChildrenRequest {
            var countContentChildrenRequest = new api.content.CountItemsWithChildrenRequest();
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

        private showDeleteResult(result: api.content.DeleteContentResult) {
            var paths = [];

            for (var i = 0; i < result.getDeleted().length; i++) {
                var path = result.getDeleted()[i];
                paths.push(path);
            }

            if (result.getDeleted().length > 0) {
                api.notify.showFeedback('Content [' + paths.map((path)=>path.toString()).join(', ') + '] deleted!');
            } else {
                var reason = result.getDeleteFailures().length > 0 ? result.getDeleteFailures()[0].getReason() : '';
                api.notify.showWarning('Content could not be deleted. ' + reason);
            }
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
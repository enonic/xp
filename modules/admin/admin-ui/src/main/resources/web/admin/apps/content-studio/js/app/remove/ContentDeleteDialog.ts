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

        private descendantsContainer: DescendantsToBeDeletedList;

        private yesCallback: () => void;

        private noCallback: () => void;

        constructor() {
            super("item");

            this.deleteButton = this.setDeleteAction(new ContentDeleteDialogAction());

            this.addDeleteActionHandler();

            this.addDescendantsContainer();

            this.addCancelButtonToBottom();
        }

        setContentToDelete(contents: ContentSummaryAndCompareStatus[]): ContentDeleteDialog {

            this.selectedItems = [];

            contents.forEach((content: ContentSummaryAndCompareStatus) => {
                this.selectedItems.push(this.createSelectionItemForDelete(content));
            });

            this.renderSelectedItems(this.selectedItems);
            this.updateSubTitle();
            if(this.selectedItems.length === 1) {
                this.selectedItems[0].hideRemoveButton();
            }

            this.descendantsContainer.hide();

            if(this.atLeastOneInitialItemHasChild()) {
                this.descendantsContainer.loadData(this.selectedItems).then(() => {
                    this.descendantsContainer.show();
                    this.centerMyself();
                });
            }

            this.countItemsToDeleteAndUpdateButtonCounter();

            return this;
        }

        setYesCallback(callback: () => void): ContentDeleteDialog {
            this.yesCallback = callback;
            return this;
        }

        setNoCallback(callback: () => void): ContentDeleteDialog {
            this.noCallback = callback;
            return this;
        }

        private addDescendantsContainer() {
            this.descendantsContainer = new DescendantsToBeDeletedList("descendants-to-delete-list");
            this.appendChildToContentPanel(this.descendantsContainer);
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

                if(!!this.yesCallback) {
                    this.yesCallback();
                }

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

            var selectionItem = new ContentDeleteSelectionItem(deleteItemViewer, browseItem, () => {
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
                    this.updateSubTitle();
                    if(this.selectedItems.length === 1) {
                        this.selectedItems[0].hideRemoveButton();
                    }

                    if(this.atLeastOneInitialItemHasChild()) {
                        this.descendantsContainer.loadData(this.selectedItems).then(() => {
                            this.centerMyself();
                        });
                    }
                    else {
                        this.descendantsContainer.hide();
                        this.centerMyself();
                    }

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
            var showCounter: boolean = this.selectedItems.length > 1 || this.atLeastOneInitialItemHasChild();
            this.deleteButton.setLabel("Delete" + (showCounter ? " (" + count + ")" : ""));
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

        private atLeastOneInitialItemHasChild(): boolean {
            return this.selectedItems.some((obj: SelectionItem<ContentSummaryAndCompareStatus>) => {
                return obj.getBrowseItem().getModel().hasChildren();
            });
        }

        private updateSubTitle() {
            if(!this.atLeastOneInitialItemHasChild()) {
                this.updateSubTitleText("");
            }
            else if(this.selectedItems.length > 1) {
                this.updateSubTitleText("Delete selected items and their child content");
            }
            else {
                this.updateSubTitleText("Delete selected item and its child content");
            }
        }
    }

    export class DescendantsToBeDeletedList extends api.ui.selector.list.ListBox<ContentSummary> {

        constructor(className?: string) {
            super(className);
        }

        loadData(selectedItems: SelectionItem<ContentSummaryAndCompareStatus>[]): wemQ.Promise<void> {
            return this.createRequestForGettingItemsDescendants(selectedItems).sendAndParse().then((result: api.content.ContentResponse<ContentSummary>) => {
                this.setItems(result.getContents());
                this.prependChild(new api.dom.H6El("descendants-header").setHtml("Other items that will be deleted"));
            });
        }

        createItemView(item: ContentSummary, readOnly: boolean): api.dom.Element {
            return new DescendantView(item);
        }

        getItemId(item: ContentSummary): string {
            return item.getId();
        }

        private createRequestForGettingItemsDescendants(selectedItems: SelectionItem<ContentSummaryAndCompareStatus>[]): api.content.GetDescendantsOfContents {
            var getDescendantsOfContentsRequest = new api.content.GetDescendantsOfContents();
            for (var j = 0; j < selectedItems.length; j++) {
                getDescendantsOfContentsRequest.addContentPath(ContentPath.fromString(selectedItems[j].getBrowseItem().getPath()));
            }

            return getDescendantsOfContentsRequest;
        }
    }

    export class DescendantView extends api.dom.DivEl {

        private wrapperDivEl: api.dom.DivEl;

        private iconImageEl: api.dom.ImgEl;

        private iconDivEl: api.dom.DivEl;

        private namesView: api.app.NamesView;

        constructor(content: api.content.ContentSummary) {
            super("names-and-icon-view descendant-view small");

            this.wrapperDivEl = new api.dom.DivEl("wrapper");
            this.appendChild(this.wrapperDivEl);

            if (!content.getType().isImage()) {
                this.iconImageEl = new api.dom.ImgEl(null, "font-icon-default");
                this.wrapperDivEl.appendChild(this.iconImageEl);
                this.iconImageEl.setSrc(this.resolveIconUrl(content));
            } else {
                this.iconDivEl = new api.dom.DivEl("font-icon-default image");
                this.wrapperDivEl.appendChild(this.iconDivEl);
            }

            this.namesView = new api.app.NamesView(false).setMainName(this.resolveDisplayName(content));
            this.appendChild(this.namesView);
        }

        private resolveDisplayName(object: ContentSummary): string {
            var contentName = object.getName(),
                invalid = !object.isValid() || !object.getDisplayName() || contentName.isUnnamed();
            this.toggleClass("invalid", invalid);

            return object.getPath().toString();
        }

        private resolveIconUrl(object: ContentSummary): string {
            return new ContentIconUrlResolver().setContent(object).resolve();
        }
    }
}
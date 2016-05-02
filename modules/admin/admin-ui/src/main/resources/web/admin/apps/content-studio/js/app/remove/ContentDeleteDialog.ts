import "../../api.ts";

import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
import BrowseItem = api.app.browse.BrowseItem;
import SelectionItem = api.app.browse.SelectionItem;
import ContentSummary = api.content.ContentSummary;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ContentPath = api.content.ContentPath;
import DialogButton = api.ui.dialog.DialogButton;
import CompareStatus = api.content.CompareStatus;
import ContentSummaryAndCompareStatusFetcher = api.content.ContentSummaryAndCompareStatusFetcher;
import PublishContentRequest = api.content.PublishContentRequest;
import {ContentDeleteDialogAction} from "./ContentDeleteDialogAction";
import {ContentDeleteSelectionItem} from "./ContentDeleteSelectionItem";
import {DeleteAction} from "../view/DeleteAction";
import {DependantView} from "../view/DependantView";
import {ConfirmContentDeleteDialog} from "./ConfirmContentDeleteDialog";

export class ContentDeleteDialog extends api.app.remove.DeleteDialog {

    private selectedItems: SelectionItem<ContentSummaryAndCompareStatus>[];

    private descendantItems: ContentSummaryAndCompareStatus[];

    private deleteButton: DialogButton;

    private descendantsContainer: DescendantsToBeDeletedList;

    private instantDeleteCheckbox: api.ui.Checkbox;

    private yesCallback: (exclude?: CompareStatus[]) => void;

    private noCallback: () => void;

    private totalItemsToDelete: number;

    constructor() {
        super("item");

        this.deleteButton = this.setDeleteAction(new ContentDeleteDialogAction());

        this.addDeleteActionHandler();

        this.addDescendantsContainer();

        this.addCancelButtonToBottom();

        this.addInstantDeleteCheckbox();
    }

    setContentToDelete(contents: ContentSummaryAndCompareStatus[]): ContentDeleteDialog {

        this.selectedItems = [];
        this.descendantItems = [];

        contents.forEach((content: ContentSummaryAndCompareStatus) => {
            this.selectedItems.push(this.createSelectionItemForDelete(content));
        });

        this.renderSelectedItems(this.selectedItems);
        this.updateSubTitle();
        if (this.onlyOneItemSelected()) {
            this.selectedItems[0].hideRemoveButton();
        }

        this.descendantsContainer.hide();
        this.atLeastOneInitialItemIsOnline() ? this.instantDeleteCheckbox.show() : this.instantDeleteCheckbox.hide();
        this.instantDeleteCheckbox.setChecked(false, true);

        if(this.atLeastOneInitialItemHasChild()) {
            this.descendantsContainer.loadData(this.selectedItems).then((descendants: ContentSummaryAndCompareStatus[]) => {
                this.descendantItems = descendants;
                this.descendantsContainer.show();
                this.centerMyself();

                if (!this.atLeastOneInitialItemIsOnline() && this.atLeastOneDescendantIsOnline(descendants)) {
                    this.instantDeleteCheckbox.show();
                }
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

    private addInstantDeleteCheckbox() {
        this.instantDeleteCheckbox = new api.ui.Checkbox("Instantly delete published items");
        this.instantDeleteCheckbox.addClass('instant-delete-check');

        this.appendChild(this.instantDeleteCheckbox);
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
            if (this.isAnySiteToBeDeleted()) {
                this.close();
                new ConfirmContentDeleteDialog({
                    totalItemsToDelete: this.totalItemsToDelete,
                    deleteRequest: this.createDeleteRequest(),
                    yesCallback: this.yesCallback
                }).open();
                return;
            }

            if(!!this.yesCallback) {
                this.instantDeleteCheckbox.isChecked() ? this.yesCallback([]) : this.yesCallback();
            }

            this.deleteButton.setEnabled(false);
            this.showLoadingSpinner();

            this.createDeleteRequest().sendAndParse().then((result: api.content.DeleteContentResult) => {
                this.close();
                DeleteAction.showDeleteResult(result);
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

            if (this.noItemsSelected()) {
                this.close();
            }
            else {
                this.updateSubTitle();
                if (this.onlyOneItemSelected()) {
                    this.selectedItems[0].hideRemoveButton();
                }

                if (this.atLeastOneInitialItemHasChild()) {
                    this.descendantsContainer.loadData(this.selectedItems).then((descendants: ContentSummaryAndCompareStatus[]) => {
                        this.descendantItems = descendants;
                        this.centerMyself();

                        if (!this.atLeastOneInitialItemIsOnline() && !this.atLeastOneDescendantIsOnline(descendants)) {
                            this.instantDeleteCheckbox.hide();
                        }
                    });
                }
                else {
                    this.descendantItems = [];
                    this.descendantsContainer.hide();
                    if (!this.atLeastOneInitialItemIsOnline()) {
                        this.instantDeleteCheckbox.hide();
                    }

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
            this.totalItemsToDelete = itemsToDeleteCounter;
            this.updateDeleteButtonCounter();
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

        this.instantDeleteCheckbox.isChecked() ? deleteRequest.setDeleteOnline(true) : deleteRequest.setDeletePending(true);

        return deleteRequest;
    }

    private updateDeleteButtonCounter() {
        var showCounter: boolean = this.moreThanOneItemSelected() || this.atLeastOneInitialItemHasChild();
        this.deleteButton.setLabel("Delete" + (showCounter ? " (" + this.totalItemsToDelete + ")" : ""));
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

    private atLeastOneInitialItemIsOnline(): boolean {
        return this.selectedItems.some((obj: SelectionItem<ContentSummaryAndCompareStatus>) => {
            return this.isContentOnline(obj.getBrowseItem().getModel().getCompareStatus());
        });
    }

    private atLeastOneDescendantIsOnline(descendants: ContentSummaryAndCompareStatus[]): boolean {
        return descendants.some((obj: ContentSummaryAndCompareStatus) => {
            return this.isContentOnline(obj.getCompareStatus());
        });
    }

    private isContentOnline(status: CompareStatus): boolean {
        return  status === CompareStatus.EQUAL ||
                status === CompareStatus.MOVED ||
                status === CompareStatus.NEWER; //except PENDING_DELETE because it gets deleted immediately
    }

    private updateSubTitle() {
        if(!this.atLeastOneInitialItemHasChild()) {
            this.updateSubTitleText("");
        }
        else if (this.moreThanOneItemSelected()) {
            this.updateSubTitleText("Delete selected items and their child content");
        }
        else {
            this.updateSubTitleText("Delete selected item and its child content");
        }
    }

    private noItemsSelected(): boolean {
        return this.selectedItems.length === 0;
    }

    private onlyOneItemSelected(): boolean {
        return this.selectedItems.length === 1;
    }

    private moreThanOneItemSelected(): boolean {
        return this.selectedItems.length > 1;
    }

    private isAnySiteToBeDeleted(): boolean {
        var result = this.selectedItems.some((selectionItem: SelectionItem<ContentSummaryAndCompareStatus>) => {
            return selectionItem.getBrowseItem().getModel().getContentSummary().isSite() &&
                   (!this.isContentOnline(selectionItem.getBrowseItem().getModel().getCompareStatus()) ||
                    this.instantDeleteCheckbox.isChecked());
        });

        if (result) {
            return true;
        }

        if (!!this.descendantItems && this.descendantItems.length > 0) {
            return this.descendantItems.some((descendant: ContentSummaryAndCompareStatus) => {
                return descendant.getContentSummary().isSite() &&
                       (!this.isContentOnline(descendant.getCompareStatus()) || this.instantDeleteCheckbox.isChecked());
            });
        }
        else {
            return false;
        }
    }
}

export class DescendantsToBeDeletedList extends api.ui.selector.list.ListBox<ContentSummary> {

    constructor(className?: string) {
        super(className);
    }

    loadData(selectedItems: SelectionItem<ContentSummaryAndCompareStatus>[]): wemQ.Promise<ContentSummaryAndCompareStatus[]> {
        return this.createRequestForGettingItemsDescendants(selectedItems).sendAndParse().then((result: api.content.ContentResponse<ContentSummary>) => {
            this.setItems(result.getContents());
            this.prependChild(new api.dom.H6El("descendants-header").setHtml("Other items that will be deleted"));

            return api.content.CompareContentRequest.fromContentSummaries(result.getContents()).sendAndParse().then((compareContentResults: api.content.CompareContentResults) => {
                return ContentSummaryAndCompareStatusFetcher.updateCompareStatus(result.getContents(), compareContentResults);
            });
        });
    }

    createItemView(item: ContentSummary, readOnly: boolean): api.dom.Element {
        return DependantView.create().item(item).build();
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



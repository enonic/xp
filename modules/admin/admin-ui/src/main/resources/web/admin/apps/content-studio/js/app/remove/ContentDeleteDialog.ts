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
import ListBox = api.ui.selector.list.ListBox;
import {ContentDeleteDialogAction} from "./ContentDeleteDialogAction";
import {ContentDeleteSelectionItem} from "./ContentDeleteSelectionItem";
import {DeleteAction} from "../view/DeleteAction";
import {DependantView} from "../view/DependantView";
import {ConfirmContentDeleteDialog} from "./ConfirmContentDeleteDialog";

export class ContentDeleteDialog extends api.app.remove.DeleteDialog<ContentSummaryAndCompareStatus> {

    private deleteButton: DialogButton;

    private dependantList: DependantList;

    private instantDeleteCheckbox: api.ui.Checkbox;

    private yesCallback: (exclude?: CompareStatus[]) => void;

    private noCallback: () => void;

    private totalItemsToDelete: number;

    constructor() {
        super("item");

        this.getItemList().onItemsRemoved(this.onListItemsRemoved.bind(this));

        this.deleteButton = this.setDeleteAction(new ContentDeleteDialogAction());

        this.addDeleteActionHandler();

        this.addCancelButtonToBottom();

        this.dependantList = new DependantList("descendants-to-delete-list");
        this.appendChildToContentPanel(this.dependantList);

        this.instantDeleteCheckbox = new api.ui.Checkbox("Instantly delete published items");
        this.instantDeleteCheckbox.addClass('instant-delete-check');

        this.appendChild(this.instantDeleteCheckbox);
    }

    protected createItemList(): ListBox<ContentSummaryAndCompareStatus> {
        return new DeletablesList();
    }

    private onListItemsRemoved(items: ContentSummaryAndCompareStatus[]) {
        var count = this.getItemList().getItemCount();

        if (count == 0) {
            this.close();

        } else {
            this.updateSubTitle();

            if (count == 1) {
                (<ContentDeleteSelectionItem>this.getItemList().getItemViews()[0]).hideRemoveButton();
            }

            var items = this.getItemList().getItems();
            if (items) {
                this.dependantList.loadData(items)
                    .then((descendants: ContentSummaryAndCompareStatus[]) => {

                        if (!this.isAnyOnline(items) && !this.isAnyOnline(descendants)) {
                            this.instantDeleteCheckbox.hide();
                        }

                        this.centerMyself();
                    });

            } else {
                this.dependantList.hide();

                if (!this.isAnyOnline(items)) {
                    this.instantDeleteCheckbox.hide();
                }

                this.centerMyself();
            }

            this.countItemsToDeleteAndUpdateButtonCounter();
        }

    }

    setContentToDelete(contents: ContentSummaryAndCompareStatus[]): ContentDeleteDialog {

        this.setListItems(contents);
        this.updateSubTitle();

        if (contents.length == 1) {
            (<ContentDeleteSelectionItem>this.getItemList().getItemView(contents[0])).hideRemoveButton();
        }

        this.dependantList.hide();

        if (this.isAnyOnline(contents)) {
            this.instantDeleteCheckbox.show();
        } else {
            this.instantDeleteCheckbox.hide();
        }
        this.instantDeleteCheckbox.setChecked(false, true);

        var items = this.getItemList().getItems();
        if (items) {
            this.dependantList.loadData(items)
                .then((descendants: ContentSummaryAndCompareStatus[]) => {
                    this.dependantList.show();
                    this.centerMyself();

                    if (!this.isAnyOnline(items) && this.isAnyOnline(descendants)) {
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

            if (!!this.yesCallback) {
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

        this.getItemList().getItems().forEach((item) => {
            countContentChildrenRequest.addContentPath(item.getContentSummary().getPath());
        });

        return countContentChildrenRequest;
    }

    private createDeleteRequest(): api.content.DeleteContentRequest {
        var deleteRequest = new api.content.DeleteContentRequest();

        this.getItemList().getItems().forEach((item) => {
            deleteRequest.addContentPath(item.getContentSummary().getPath());
        });

        this.instantDeleteCheckbox.isChecked() ? deleteRequest.setDeleteOnline(true) : deleteRequest.setDeletePending(true);

        return deleteRequest;
    }

    private updateDeleteButtonCounter() {
        var items = this.getItemList().getItems(),
            count = items.length;

        var showCounter: boolean = count > 1 || this.doAnyHaveChildren(items);
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

    private doAnyHaveChildren(items: ContentSummaryAndCompareStatus[]): boolean {
        return items.some((item: ContentSummaryAndCompareStatus) => {
            return item.getContentSummary().hasChildren();
        });
    }

    private isAnyOnline(items: ContentSummaryAndCompareStatus[]): boolean {
        return items.some((item: ContentSummaryAndCompareStatus) => {
            return this.isStatusOnline(item.getCompareStatus());
        });
    }

    private isStatusOnline(status: CompareStatus): boolean {
        return status === CompareStatus.EQUAL ||
               status === CompareStatus.MOVED ||
               status === CompareStatus.NEWER; //except PENDING_DELETE because it gets deleted immediately
    }

    updateSubTitle() {
        var items = this.getItemList().getItems(),
            count = items.length;

        if (!this.doAnyHaveChildren(items)) {
            super.updateSubTitle("");
        } else {
            super.updateSubTitle(`Delete selected items and ${count > 1 ? 'their' : 'its'} child content`);
        }
    }


    private isAnySiteToBeDeleted(): boolean {
        var result = this.getItemList().getItems().some((item: ContentSummaryAndCompareStatus) => {
            return item.getContentSummary().isSite() &&
                   (!this.isStatusOnline(item.getCompareStatus()) || this.instantDeleteCheckbox.isChecked());
        });

        if (result) {
            return true;
        }

        if (this.dependantList.getItemCount() > 0) {
            return this.dependantList.getItems().some((descendant: ContentSummaryAndCompareStatus) => {
                return descendant.getContentSummary().isSite() &&
                       (!this.isStatusOnline(descendant.getCompareStatus()) || this.instantDeleteCheckbox.isChecked());
            });
        } else {
            return false;
        }
    }

}

export class DeletablesList extends ListBox<ContentSummaryAndCompareStatus> {

    constructor(className?: string) {
        super(className);
    }

    createItemView(item: ContentSummaryAndCompareStatus, readOnly: boolean): api.dom.Element {
        var deleteItemViewer = new api.content.ContentSummaryAndCompareStatusViewer();
        deleteItemViewer.setObject(item);

        var browseItem = new BrowseItem<ContentSummaryAndCompareStatus>(item).
            setId(item.getId()).
            setDisplayName(item.getDisplayName()).
            setPath(item.getPath().toString()).
            setIconUrl(new ContentIconUrlResolver().setContent(item.getContentSummary()).resolve());


        var selectionItem = new ContentDeleteSelectionItem(deleteItemViewer, browseItem, () => {
            this.removeItem(item);
        });

        return selectionItem;
    }

    getItemId(item: ContentSummaryAndCompareStatus): string {
        return item.getContentSummary().getId();
    }

}

export class DependantList extends ListBox<ContentSummaryAndCompareStatus> {

    constructor(className?: string) {
        super(className);
    }

    loadData(summaries: ContentSummaryAndCompareStatus[]): wemQ.Promise<ContentSummaryAndCompareStatus[]> {
        return this.createRequestForGettingItemsDescendants(summaries).sendAndParse()
            .then((result: api.content.ContentResponse<ContentSummary>) => {

                return api.content.CompareContentRequest.fromContentSummaries(result.getContents()).sendAndParse()
                    .then((compareContentResults: api.content.CompareContentResults) => {

                        var summaries = ContentSummaryAndCompareStatusFetcher
                            .updateCompareStatus(result.getContents(), compareContentResults);

                        this.setItems(summaries);
                        this.prependChild(new api.dom.H6El("descendants-header").setHtml("Other items that will be deleted"));

                        return summaries;
                    });
            });
    }

    createItemView(item: ContentSummaryAndCompareStatus, readOnly: boolean): api.dom.Element {
        return DependantView.create().item(item.getContentSummary()).build();
    }

    getItemId(item: ContentSummaryAndCompareStatus): string {
        return item.getContentSummary().getId();
    }

    private createRequestForGettingItemsDescendants(summaries: ContentSummaryAndCompareStatus[]): api.content.GetDescendantsOfContents {
        var getDescendantsOfContentsRequest = new api.content.GetDescendantsOfContents();

        summaries.forEach((summary) => {
            getDescendantsOfContentsRequest.addContentPath(summary.getContentSummary().getPath());
        });

        return getDescendantsOfContentsRequest;
    }
}



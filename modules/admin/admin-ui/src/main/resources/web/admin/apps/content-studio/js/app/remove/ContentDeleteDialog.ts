import "../../api.ts";
import {DeleteAction} from "../view/DeleteAction";
import {DependantItemsDialog} from "../dialog/DependantItemsDialog";
import {ContentDeleteDialogAction} from "./ContentDeleteDialogAction";
import {ConfirmContentDeleteDialog} from "./ConfirmContentDeleteDialog";

import ContentSummary = api.content.ContentSummary;
import CompareStatus = api.content.CompareStatus;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import DialogButton = api.ui.dialog.DialogButton;
import ListBox = api.ui.selector.list.ListBox;

export class ContentDeleteDialog extends DependantItemsDialog {

    private deleteButton: DialogButton;

    private instantDeleteCheckbox: api.ui.Checkbox;

    private yesCallback: (exclude?: CompareStatus[]) => void;

    private noCallback: () => void;

    private totalItemsToDelete: number;

    constructor() {
        super("Delete item",
            "Delete selected items and their children",
            "Other items that will be deleted");

        this.addClass("delete-dialog");

        this.getItemList().onItemsRemoved(this.onListItemsRemoved.bind(this));

        let deleteAction = new ContentDeleteDialogAction();
        this.addDeleteActionHandler(deleteAction);
        this.deleteButton = this.addAction(deleteAction, true, true);

        this.addCancelButtonToBottom();

        this.instantDeleteCheckbox = new api.ui.Checkbox("Instantly delete published items");
        this.instantDeleteCheckbox.addClass('instant-delete-check');

        this.appendChild(this.instantDeleteCheckbox);
    }

    private onListItemsRemoved(items: ContentSummaryAndCompareStatus[]) {
        if (this.isIgnoreItemsChanged()) {
            return;
        }

        this.updateSubTitle();

        var items = this.getItemList().getItems();

        this.manageDescendants(items);

        this.countItemsToDeleteAndUpdateButtonCounter();
    }

    protected manageDescendants(items: ContentSummaryAndCompareStatus[]) {
        this.loadMask.show();
        this.deleteButton.setEnabled(false);
        this.loadDescendants(items)
            .then((descendants: ContentSummaryAndCompareStatus[]) => {

                this.setDependantItems(descendants);

                if (!this.isAnyOnline(items)) {
                    this.manageInstantDeleteStatus(descendants);
                }

                this.centerMyself();
            }).finally(() => {
                this.loadMask.hide();
                this.deleteButton.setEnabled(true);
            });
    }

    private manageInstantDeleteStatus(items: ContentSummaryAndCompareStatus[]) {
        const isVisible = this.isAnyOnline(items);
        this.instantDeleteCheckbox.setVisible(isVisible);
        if (isVisible) {
            const isChecked = this.isEveryPendingDelete(items);
            this.instantDeleteCheckbox.setChecked(isChecked, true);
            // Disable to prevent uncheck, when every content is pending delete
            this.instantDeleteCheckbox.setDisabled(isChecked);
        } else {
            this.instantDeleteCheckbox.setChecked(false, true);
        }
    }

    setContentToDelete(contents: ContentSummaryAndCompareStatus[]): ContentDeleteDialog {
        this.setIgnoreItemsChanged(true);
        this.setListItems(contents);
        this.setIgnoreItemsChanged(false);
        this.updateSubTitle();

        this.manageInstantDeleteStatus(contents);

        this.manageDescendants(contents);

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


    private addDeleteActionHandler(deleteAction: api.ui.Action) {
        deleteAction.onExecuted(() => {
            if (this.isAnySiteToBeDeleted()) {
                let totalItemsToDelete = this.totalItemsToDelete,
                    deleteRequest = this.createDeleteRequest(),
                    yesCallback = this.yesCallback;

                this.close();
                
                new ConfirmContentDeleteDialog({
                    totalItemsToDelete: totalItemsToDelete,
                    deleteRequest: deleteRequest,
                    yesCallback: yesCallback
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
        this.deleteButton.setLabel("Delete ");
        this.showLoadingSpinner();

        this.createCountToDeleteRequest().sendAndParse().then((countToDelete: number) => {
            this.hideLoadingSpinner();
            this.totalItemsToDelete = countToDelete;
            this.updateDeleteButtonCounter(countToDelete);
        }).finally(() => {
            this.hideLoadingSpinner();
        }).done();
    }


    private createCountToDeleteRequest(): api.content.CountContentsWithDescendantsRequest {
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

        deleteRequest.setDeleteOnline(this.instantDeleteCheckbox.isChecked());

        return deleteRequest;
    }

    private updateDeleteButtonCounter(count) {
        var items = this.getItemList().getItems();

        var showCounter: boolean = count > 1 || this.doAnyHaveChildren(items);
        this.deleteButton.setLabel("Delete" + (showCounter ? " (" + count + ")" : ""));
    }

    private showLoadingSpinner() {
        this.deleteButton.addClass("spinner");
    }

    private hideLoadingSpinner() {
        this.deleteButton.removeClass("spinner");
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

    private isEveryPendingDelete(items: ContentSummaryAndCompareStatus[]): boolean {
        return items.every((item: ContentSummaryAndCompareStatus) => {
            return this.isStatusPendingDelete(item.getCompareStatus());
        });
    }

    private isStatusOnline(status: CompareStatus): boolean {
        return status === CompareStatus.EQUAL ||
               status === CompareStatus.MOVED ||
               status === CompareStatus.NEWER ||
               status === CompareStatus.PENDING_DELETE;
    }

    private isStatusPendingDelete(status: CompareStatus): boolean {
        return status === CompareStatus.PENDING_DELETE;
    }

    private updateSubTitle() {
        var items = this.getItemList().getItems(),
            count = items.length;

        if (!this.doAnyHaveChildren(items)) {
            super.setSubTitle("");
        } else {
            super.setSubTitle(`Delete selected items and ${count > 1 ? 'their' : 'its'} child content`);
        }
    }


    private isAnySiteToBeDeleted(): boolean {
        var result = this.getItemList().getItems().some((item: ContentSummaryAndCompareStatus) => {
            return item.getContentSummary().isSite();
        });

        if (result) {
            return true;
        }

        var dependantList = this.getDependantList();
        if (dependantList.getItemCount() > 0) {
            return dependantList.getItems().some((descendant: ContentSummaryAndCompareStatus) => {
                return descendant.getContentSummary().isSite();
            });
        } else {
            return false;
        }
    }

}



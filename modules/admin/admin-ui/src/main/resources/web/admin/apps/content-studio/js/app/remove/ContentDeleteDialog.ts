import "../../api.ts";
import {ContentDeleteDialogAction} from "./ContentDeleteDialogAction";
import {ConfirmContentDeleteDialog} from "./ConfirmContentDeleteDialog";
import {ProgressBarDialog} from "../dialog/ProgressBarDialog";
import {ContentDeletePromptEvent} from "../browse/ContentDeletePromptEvent";

import ContentSummary = api.content.ContentSummary;
import CompareStatus = api.content.CompareStatus;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import DialogButton = api.ui.dialog.DialogButton;
import ListBox = api.ui.selector.list.ListBox;

export class ContentDeleteDialog extends ProgressBarDialog {

    private instantDeleteCheckbox: api.ui.Checkbox;

    private yesCallback: (exclude?: CompareStatus[]) => void;

    private noCallback: () => void;

    private totalItemsToDelete: number;

    constructor() {
        super("Delete item",
            "Delete selected items and their children",
            "Other items that will be deleted",
            "is-deleting",
            () => {
                new ContentDeletePromptEvent([]).fire();
            }
        );

        this.addClass("delete-dialog");

        this.getItemList().onItemsRemoved(this.onListItemsRemoved.bind(this));

        let deleteAction = new ContentDeleteDialogAction();
        deleteAction.onExecuted(this.doDelete.bind(this, false));
        this.actionButton = this.addAction(deleteAction, true, true);

        this.addCancelButtonToBottom();

        this.instantDeleteCheckbox = api.ui.Checkbox.create().setLabelText("Instantly delete published items").build();
        this.instantDeleteCheckbox.addClass('instant-delete-check');

        this.appendChild(this.instantDeleteCheckbox);
    }

    private onListItemsRemoved() {
        if (this.isIgnoreItemsChanged()) {
            return;
        }

        this.updateSubTitle();

        this.manageDescendants();
    }

    protected manageDescendants() {
        this.loadMask.show();
        this.actionButton.setEnabled(false);

        return this.loadDescendantIds().then(() => {
            this.loadDescendants(0, 20).
                then((descendants: ContentSummaryAndCompareStatus[]) => {
                    this.setDependantItems(descendants);
                this.manageInstantDeleteStatus(this.getItemList().getItems());
                    this.countItemsToDeleteAndUpdateButtonCounter();
                    this.centerMyself();
                }).finally(() => {
                    this.loadMask.hide();
                    this.actionButton.setEnabled(true);
                this.actionButton.giveFocus();
                });
        });
    }

    private manageInstantDeleteStatus(items: ContentSummaryAndCompareStatus[]) {
        const isHidden = this.isEveryOffline(items);
        const isChecked = isHidden ? false : this.isEveryPendingDelete(items);

        // All Offline - hidden
        // All Pending Delete - hidden, checked
        // Any Online - unchecked
        this.instantDeleteCheckbox.setVisible(!isHidden && !isChecked);
        this.instantDeleteCheckbox.setChecked(isChecked, true);
    }

    close() {
        super.close();
        this.instantDeleteCheckbox.setChecked(false);
    }

    setContentToDelete(contents: ContentSummaryAndCompareStatus[]): ContentDeleteDialog {
        this.setIgnoreItemsChanged(true);
        this.setListItems(contents);
        this.setIgnoreItemsChanged(false);
        this.updateSubTitle();

        this.manageInstantDeleteStatus(contents);
        
        this.manageDescendants();

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

    private doDelete(ignoreConfirmation: boolean = false) {
        if (this.isAnySiteToBeDeleted() && !ignoreConfirmation) {
            const totalItemsToDelete = this.totalItemsToDelete;
            const deleteRequest = this.createDeleteRequest();
            const content = this.getItemList().getItems().slice(0);
            const descendants = this.getDependantList().getItems().slice(0);
            const instantDeleteStatus = this.instantDeleteCheckbox.isChecked();
            const yesCallback = () => {
                this.setContentToDelete(content);
                this.setDependantItems(descendants);
                this.countItemsToDeleteAndUpdateButtonCounter();
                this.open();
                this.instantDeleteCheckbox.setChecked(instantDeleteStatus);
                this.doDelete(true);
            };

            this.close();

            new ConfirmContentDeleteDialog({totalItemsToDelete, deleteRequest, yesCallback}).open();
        } else {
            if (this.yesCallback) {
                this.instantDeleteCheckbox.isChecked() ? this.yesCallback([]) : this.yesCallback();
            }

            this.showLoadingSpinner();

            this.createDeleteRequest()
                .sendAndParse()
                .then((taskId: api.task.TaskId) => {
                    this.pollTask(taskId);
                })
                .catch((reason) => {
                    this.close();
                    if (reason && reason.message) {
                        api.notify.showError(reason.message);
                    }
                });
        }
    }

    private countItemsToDeleteAndUpdateButtonCounter() {
        this.actionButton.setLabel("Delete ");

        this.totalItemsToDelete = this.countTotal();
        this.updateButtonCount("Delete", this.totalItemsToDelete);
    }

    private createDeleteRequest(): api.content.resource.DeleteContentRequest {
        var deleteRequest = new api.content.resource.DeleteContentRequest();

        this.getItemList().getItems().forEach((item) => {
            deleteRequest.addContentPath(item.getContentSummary().getPath());
        });

        deleteRequest.setDeleteOnline(this.instantDeleteCheckbox.isChecked());

        return deleteRequest;
    }

    protected updateButtonCount(actionString: string, count:number) {
        super.updateButtonCount(actionString, count);
    }

    private doAnyHaveChildren(items: ContentSummaryAndCompareStatus[]): boolean {
        return items.some((item: ContentSummaryAndCompareStatus) => {
            return item.getContentSummary().hasChildren();
        });
    }

    private isEveryOffline(items: ContentSummaryAndCompareStatus[]): boolean {
        return items.every((item: ContentSummaryAndCompareStatus) => {
            return this.isStatusOffline(item.getCompareStatus());
        });
    }

    private isEveryPendingDelete(items: ContentSummaryAndCompareStatus[]): boolean {
        return items.every((item: ContentSummaryAndCompareStatus) => {
            return this.isStatusPendingDelete(item.getCompareStatus());
        });
    }

    private isStatusOffline(status: CompareStatus): boolean {
        return status === CompareStatus.NEW;
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



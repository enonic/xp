import "../../api.ts";
import {ProgressBarDialog} from "../dialog/ProgressBarDialog";
import {ContentUnpublishPromptEvent} from "../browse/ContentUnpublishPromptEvent";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import DialogButton = api.ui.dialog.DialogButton;
import UnpublishContentRequest = api.content.resource.UnpublishContentRequest;
import ResolvePublishDependenciesResult = api.content.resource.result.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ContentId = api.content.ContentId;
import ListBox = api.ui.selector.list.ListBox;


export class ContentUnpublishDialog extends ProgressBarDialog {


    constructor() {

        super(
            "Unpublish item",
            "<b>Take offline?</b> - Unpublishing selected item(s) will set status back to offline",
            "Dependent items - Clean up references to selected item(s) or click unpublish to take all items offline",
            "is-unpublishing",
            () => {
                new ContentUnpublishPromptEvent([]).fire();
            }
        );

        this.getEl().addClass("unpublish-dialog");

        var unpublishAction = new ContentUnpublishDialogAction();
        unpublishAction.onExecuted(this.doUnpublish.bind(this));
        this.actionButton = this.addAction(unpublishAction, true, true);
        this.actionButton.setEnabled(false);

        this.addCancelButtonToBottom();

        this.getItemList().onItemsRemoved((items: ContentSummaryAndCompareStatus[]) => {
            if (!this.isIgnoreItemsChanged()) {
                this.reloadUnpublishDependencies().done();
            }
        });
    }

    open() {
        this.reloadUnpublishDependencies().done(() => this.centerMyself());

        super.open();
    }

    private reloadUnpublishDependencies(): wemQ.Promise<void> {
        if (this.isProgressBarEnabled()) {
            return wemQ<void>(null);
        }

        this.getDependantList().clearItems();
        this.showLoadingSpinner();
        this.actionButton.setEnabled(false);

        return this.loadDescendantIds([CompareStatus.EQUAL,CompareStatus.NEWER,CompareStatus.PENDING_DELETE]).then(() => {
            this.loadDescendants(0, 20).
                then((items: ContentSummaryAndCompareStatus[]) => {
                    this.setDependantItems(items);

                    // do not set requested contents as they are never going to change

                    this.hideLoadingSpinner();
                    this.actionButton.setEnabled(true);
                }).finally(() => {
                    this.loadMask.hide();
                });
        });

    }

    private filterUnpublishableItems(items: ContentSummaryAndCompareStatus[]): ContentSummaryAndCompareStatus[] {
        return items.filter(item => {
            let status = item.getCompareStatus();
            return status == CompareStatus.EQUAL || status == CompareStatus.NEWER || status == CompareStatus.PENDING_DELETE ||
                   status == CompareStatus.OLDER;
        });
    }

    setDependantItems(items: ContentSummaryAndCompareStatus[]) {
        super.setDependantItems(this.filterUnpublishableItems(items));

        this.updateButtonCount("Unpublish", this.countTotal());
    }

    addDependantItems(items: ContentSummaryAndCompareStatus[]) {
        super.addDependantItems(this.filterUnpublishableItems(items));

        this.updateButtonCount("Unpublish", this.countTotal());
    }

    setContentToUnpublish(contents: ContentSummaryAndCompareStatus[]) {
        this.setIgnoreItemsChanged(true);
        this.setListItems(this.filterUnpublishableItems(contents));
        this.setIgnoreItemsChanged(false);
        return this;
    }

    private getContentToUnpublishIds(): ContentId[] {
        return this.getItemList().getItems().map(item => {
            return item.getContentId();
        })
    }

    private doUnpublish() {

        this.showLoadingSpinner();

        this.setSubTitle(this.countTotal() + " items are being unpublished...");

        var selectedIds = this.getContentToUnpublishIds();

        new UnpublishContentRequest()
            .setIncludeChildren(true)
            .setIds(selectedIds)
            .sendAndParse()
            .then((taskId: api.task.TaskId) => {
                this.pollTask(taskId);
            }).catch((reason) => {
            this.hideLoadingSpinner();
            this.close();
            if (reason && reason.message) {
                api.notify.showError(reason.message);
            }
        });
    }
}

export class ContentUnpublishDialogAction extends api.ui.Action {
    constructor() {
        super("Unpublish");
        this.setIconClass("unpublish-action");
    }
}

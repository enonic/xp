import "../../api.ts";
import {DependantItemsDialog} from "../dialog/DependantItemsDialog";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import DialogButton = api.ui.dialog.DialogButton;
import UnpublishContentRequest = api.content.UnpublishContentRequest;
import ResolvePublishDependenciesResult = api.content.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ContentId = api.content.ContentId;
import ContentPublishItem = api.content.ContentPublishItem;
import ListBox = api.ui.selector.list.ListBox;


export class ContentUnpublishDialog extends DependantItemsDialog {


    constructor() {
        super("Unpublish item",
            "<b>Take offline?</b> - Unpublishing selected item(s) will set status back to offline",
            "Dependent items - Clean up references to selected item(s) or click unpublish to take all items offline");

        this.getEl().addClass("unpublish-dialog");

        var unpublishAction = new ContentUnpublishDialogAction();
        unpublishAction.onExecuted(this.doUnpublish.bind(this));
        this.actionButton = this.addAction(unpublishAction, true, true);
        this.actionButton.setEnabled(false);

        this.addCancelButtonToBottom();

        this.getItemList().onItemsRemoved((items: ContentSummaryAndCompareStatus[]) => {
            if (!this.isIgnoreItemsChanged()) {
                this.refreshUnpublishDependencies().done();
            }
        });
    }

    open() {
        this.refreshUnpublishDependencies().done(() => this.centerMyself());

        super.open();
    }

    protected loadDescendants(from?: number,
                              size?: number, filterStatuses?:CompareStatus[]): wemQ.Promise<ContentSummaryAndCompareStatus[]> {
        return super.loadDescendants(from, size, [CompareStatus.EQUAL,CompareStatus.NEWER,CompareStatus.PENDING_DELETE]);
    }


    private refreshUnpublishDependencies(): wemQ.Promise<void> {

        this.getDependantList().clearItems();
        this.showLoadingSpinner();
        this.actionButton.setEnabled(false);

        return this.loadDescendants().then((summaries: ContentSummaryAndCompareStatus[]) => {
            this.setDependantItems(this.filterUnpublishableItems(summaries));

            // do not set requested contents as they are never going to change

            this.hideLoadingSpinner();
            this.actionButton.setEnabled(true);
        }).finally(() => {
            this.loadMask.hide();
        });

    }

    private filterUnpublishableItems(items: ContentSummaryAndCompareStatus[]): ContentSummaryAndCompareStatus[] {
        return items.filter(item => {
            let status = item.getCompareStatus();
            return status == CompareStatus.EQUAL || status == CompareStatus.NEWER || status == CompareStatus.PENDING_DELETE;
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
        this.actionButton.setEnabled(false);

        var selectedIds = this.getContentToUnpublishIds();

        new UnpublishContentRequest().setIds(selectedIds).setIncludeChildren(true).send().then(
            (jsonResponse: api.rest.JsonResponse<api.content.UnpublishContentResult>) => {
                this.close();
                UnpublishContentRequest.feedback(jsonResponse);
            }).finally(() => {
                this.hideLoadingSpinner();
                this.actionButton.setEnabled(true);
            });
    }
}

export class ContentUnpublishDialogAction extends api.ui.Action {
    constructor() {
        super("Unpublish");
        this.setIconClass("unpublish-action");
    }
}

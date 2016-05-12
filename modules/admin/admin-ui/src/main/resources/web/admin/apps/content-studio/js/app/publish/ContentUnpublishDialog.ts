import "../../api.ts";
import {DependantItemsDialog} from "../dialog/DependantItemsDialog";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import DialogButton = api.ui.dialog.DialogButton;
import PublishContentRequest = api.content.PublishContentRequest;
import ResolvePublishDependenciesResult = api.content.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ContentId = api.content.ContentId;
import ContentPublishItem = api.content.ContentPublishItem;
import ListBox = api.ui.selector.list.ListBox;
import {DependantItemsDialog} from "../dialog/DependantItemsDialog";


export class ContentUnpublishDialog extends DependantItemsDialog {

    private unpublishButton: DialogButton;

    constructor() {
        super("Unpublish item",
            "<b>Take offline?</b> - Unpublishing selected item(s) will set status back to offline",
            "Dependent items - Clean up references to selected item(s) or click unpublish to take all items offline");

        this.getEl().addClass("unpublish-dialog");

        var unpublishAction = new ContentUnpublishDialogAction();
        unpublishAction.onExecuted(this.doUnpublish.bind(this));
        this.unpublishButton = this.addAction(unpublishAction, true, true);
        this.unpublishButton.setEnabled(false);

        this.addCancelButtonToBottom();

        this.getItemList().onItemsRemoved((items: ContentSummaryAndCompareStatus[]) => {
            if (!this.isIgnoreItemsChanged()) {
                this.refreshPublishDependencies().done();
            }
        });
    }

    open() {
        this.refreshPublishDependencies().done(() => this.centerMyself());

        super.open();
    }


    private refreshPublishDependencies(): wemQ.Promise<void> {

        this.getDependantList().clearItems();
        this.showLoadingSpinnerAtButton();
        this.unpublishButton.setEnabled(false);

        return this.loadDescendants(this.getItemList().getItems()).then((summaries: ContentSummaryAndCompareStatus[]) => {

            this.setDependantItems(this.filterUnpublishableItems(summaries));

            // do not set requested contents as they are never going to change

            this.hideLoadingSpinnerAtButton();
            this.unpublishButton.setEnabled(true);
        });

    }

    private filterUnpublishableItems(items: ContentSummaryAndCompareStatus[]): ContentSummaryAndCompareStatus[] {
        return items.filter(item => {
            let status = item.getCompareStatus();
            return status == CompareStatus.EQUAL || status == CompareStatus.NEWER;
        });
    }

    setDependantItems(items: ContentSummaryAndCompareStatus[]) {
        super.setDependantItems(this.filterUnpublishableItems(items));

        let count = this.countTotal();
        this.unpublishButton.setLabel(count > 0 ? "Unpublish (" + count + ")" : "Unpublish");
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

        this.showLoadingSpinnerAtButton();
        this.unpublishButton.setEnabled(false);

        var selectedIds = this.getContentToUnpublishIds();

        //TODO: make request and hide spinner and this.close();
    }

    private countTotal(): number {
        return this.getItemList().getItemCount()
               + this.getDependantList().getItemCount();
    }

    private showLoadingSpinnerAtButton() {
        this.unpublishButton.addClass("spinner");
    }

    private hideLoadingSpinnerAtButton() {
        this.unpublishButton.removeClass("spinner");
    }

}

export class ContentUnpublishDialogAction extends api.ui.Action {
    constructor() {
        super("Unpublish");
        this.setIconClass("unpublish-action");
    }
}

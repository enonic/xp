import "../../api.ts";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import DialogButton = api.ui.dialog.DialogButton;
import PublishContentRequest = api.content.PublishContentRequest;
import ResolvePublishDependenciesResult = api.content.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ContentId = api.content.ContentId;
import ContentPublishItem = api.content.ContentPublishItem;
import ListBox = api.ui.selector.list.ListBox;
import {DependantItemsDialog, DialogDependantList} from "../dialog/DependantItemsDialog";

/**
 * ContentPublishDialog manages list of initially checked (initially requested) items resolved via ResolvePublishDependencies command.
 * Resolved items are converted into array of SelectionPublishItem<ContentPublishItem> items and stored in selectionItems property.
 * ContentPublishItem contains info for the initially checked item with number of children and dependants items that will get published with it.
 * Dependant items number will change depending on includeChildren checkbox state as
 * resolved dependencies usually differ in that case.
 */
export class ContentPublishDialog extends DependantItemsDialog {

    private publishButton: DialogButton;

    private childrenCheckbox: api.ui.Checkbox;

    private excludedIds: ContentId[] = [];

    // stashes previous checkbox state items, until selected items changed
    private stash: {[checked:string]:ContentSummaryAndCompareStatus[]} = {};

    constructor() {
        super("Publishing Wizard", "Resolving items...", "Other items that will be published");

        this.setAutoUpdateTitle(false);
        this.getEl().addClass("publish-dialog");

        var publishAction = new ContentPublishDialogAction();
        publishAction.onExecuted(this.doPublish.bind(this));
        this.publishButton = this.addAction(publishAction, true, true);
        this.publishButton.setEnabled(false);

        this.addCancelButtonToBottom();

        this.initChildrenCheckbox();

        this.getItemList().onItemsRemoved((items: ContentSummaryAndCompareStatus[]) => {
            if (!this.isIgnoreItemsChanged()) {
                // clear the stash because it is no longer valid
                this.clearStashedItems();

                this.refreshPublishDependencies().done();
            }
        });
    }


    protected createDependantList(): ListBox<ContentSummaryAndCompareStatus> {
        let dependants = new PublishDialogDependantList();

        dependants.onItemClicked((item: ContentSummaryAndCompareStatus) => {
            if (!isContentSummaryValid(item)) {
                new api.content.event.EditContentEvent([item]).fire();
                this.close();
            }
        });

        dependants.onItemRemoveClicked((item: ContentSummaryAndCompareStatus) => {
            this.excludedIds.push(item.getContentId());

            // clear the stash because it is no longer valid
            this.clearStashedItems();

            this.refreshPublishDependencies().done();
        });

        return dependants;
    }

    open() {
        if (!this.doAnyHaveChildren(this.getItemList().getItems())) {

            this.childrenCheckbox.setVisible(false);
            this.getButtonRow().addClass("no-checkbox");
        }

        this.clearStashedItems();

        this.refreshPublishDependencies().done(() => this.centerMyself());

        this.excludedIds = [];

        super.open();
    }

    private getStashedItems(): ContentSummaryAndCompareStatus[] {
        return this.stash[String(this.childrenCheckbox.isChecked())];
    }

    private setStashedItems(items: ContentSummaryAndCompareStatus[]) {
        this.stash[String(this.childrenCheckbox.isChecked())] = items.slice();
    }

    private clearStashedItems() {
        this.stash = {};
    }

    private refreshPublishDependencies(): wemQ.Promise<void> {

        let stashedItems = this.getStashedItems(),
            dependantList = this.getDependantList();

        // null - means we just opened or we had to clear it because of selection change
        if (!stashedItems) {
            dependantList.clearItems();
            this.showLoadingSpinnerAtButton();
            this.publishButton.setEnabled(false);

            let ids = this.getContentToPublishIds(),
                flag = this.childrenCheckbox.isChecked(),
                resolveDependenciesRequest = new api.content.ResolvePublishDependenciesRequest(ids, this.excludedIds, flag);

            return resolveDependenciesRequest.sendAndParse().then((result: ResolvePublishDependenciesResult) => {

                var dependants = result.getDependants().map(dependant => dependant.toContentSummaryAndCompareStatus());
                this.setDependantItems(dependants);
                this.setStashedItems(dependants.slice());

                // do not set requested contents as they are never going to change,
                // but returned data contains less info than original summaries
                this.childrenCheckbox.setVisible(this.doAnyHaveChildren(this.getItemList().getItems()));

                this.hideLoadingSpinnerAtButton();
            });

        } else {
            // apply the stash to avoid extra heavy request
            this.setDependantItems(stashedItems.slice());

            return wemQ<void>(null);
        }
    }


    setDependantItems(items: api.content.ContentSummaryAndCompareStatus[]) {
        super.setDependantItems(items);

        let count = this.countTotalToPublish();

        this.updateSubTitle(count);
        this.updatePublishButton(count);

        if (this.extendsWindowHeightSize()) {
            this.centerMyself();
        }
    }

    setContentToPublish(contents: ContentSummaryAndCompareStatus[]) {
        this.setIgnoreItemsChanged(true);
        this.setListItems(contents);
        this.setIgnoreItemsChanged(false);
        return this;
    }

    setIncludeChildItems(include: boolean) {
        this.childrenCheckbox.setChecked(include);
        return this;
    }


    private initChildrenCheckbox() {

        let childrenCheckboxListener = () => this.refreshPublishDependencies().done();

        this.childrenCheckbox = new api.ui.Checkbox('Include child items');
        this.childrenCheckbox.addClass('include-child-check');
        this.childrenCheckbox.onValueChanged(childrenCheckboxListener);

        this.overwriteDefaultArrows(this.childrenCheckbox);

        this.appendChildToContentPanel(this.childrenCheckbox);
    }

    private getContentToPublishIds(): ContentId[] {
        return this.getItemList().getItems().map(item => {
            return item.getContentId();
        })
    }

    private extendsWindowHeightSize(): boolean {
        if (this.getResponsiveItem().isInRangeOrBigger(api.ui.responsive.ResponsiveRanges._540_720)) {
            var el = this.getEl(),
                bottomPosition: number = (el.getTopPx() || parseFloat(el.getComputedProperty('top')) || 0) +
                                         el.getMarginTop() +
                                         el.getHeightWithBorder() +
                                         el.getMarginBottom();

            if (window.innerHeight < bottomPosition) {
                return true;
            }
        }
        return false;
    }

    private doPublish() {

        this.showLoadingSpinnerAtButton();
        this.publishButton.setEnabled(false);

        var selectedIds = this.getContentToPublishIds();

        new PublishContentRequest().setIncludeChildren(this.childrenCheckbox.isChecked())
            .setIds(selectedIds).
            setExcludedIds(this.excludedIds).send().done(
            (jsonResponse: api.rest.JsonResponse<api.content.PublishContentResult>) => {
                this.close();
                PublishContentRequest.feedback(jsonResponse);
                new api.content.event.ContentsPublishedEvent(selectedIds).fire();
            });
    }

    private countTotalToPublish(): number {
        return this.countToPublish(this.getItemList().getItems())
               + this.countToPublish(this.getDependantList().getItems());
    }

    private countToPublish(summaries: ContentSummaryAndCompareStatus[]): number {
        return summaries.reduce((count, summary: ContentSummaryAndCompareStatus) => {
            return summary.getCompareStatus() != CompareStatus.EQUAL ? ++count : count;
        }, 0);
    }

    private updateSubTitle(count: number) {
        let allValid = this.areItemsAndDependantsValid();

        let subTitle = count == 0
            ? "No items to publish"
            : allValid ? "Your changes are ready for publishing"
                           : "Invalid content(s) prevent publish";

        this.setSubTitle(subTitle);
        this.toggleClass("invalid", !allValid);
    }

    private updatePublishButton(count: number) {
        this.publishButton.setLabel(count > 0 ? "Publish (" + count + ")" : "Publish");

        let canPublish = count > 0 && this.areItemsAndDependantsValid();

        this.publishButton.setEnabled(canPublish);
        if (canPublish) {
            this.getButtonRow().focusDefaultAction();
            this.updateTabbable();
        }
    }

    private areAllValid(summaries: ContentSummaryAndCompareStatus[]): boolean {
        return summaries.every((summary: ContentSummaryAndCompareStatus) => isContentSummaryValid(summary));
    }

    private doAnyHaveChildren(items: ContentSummaryAndCompareStatus[]): boolean {
        return items.some((item: ContentSummaryAndCompareStatus) => {
            return item.hasChildren();
        });
    }

    private areItemsAndDependantsValid(): boolean {
        var itemsValid = this.areAllValid(this.getItemList().getItems());
        if (!itemsValid) {
            return false;
        }

        return this.areAllValid(this.getDependantList().getItems());
    }

    private showLoadingSpinnerAtButton() {
        this.publishButton.addClass("spinner");
    }

    private hideLoadingSpinnerAtButton() {
        this.publishButton.removeClass("spinner");
    }

}

export class ContentPublishDialogAction extends api.ui.Action {
    constructor() {
        super("Publish");
        this.setIconClass("publish-action");
    }
}

export class PublishDialogDependantList extends DialogDependantList {

    private itemClickListeners: {(item: ContentSummaryAndCompareStatus): void}[] = [];

    private removeClickListeners: {(item: ContentSummaryAndCompareStatus): void}[] = [];


    clearItems() {
        this.removeClass("contains-removable");
        super.clearItems();
    }

    createItemView(item: api.content.ContentSummaryAndCompareStatus, readOnly: boolean): api.dom.Element {
        let view = super.createItemView(item, readOnly);

        if(CompareStatus.NEWER == item.getCompareStatus()) {
            view.addClass("removable");
            if(!this.hasClass("contains-removable")) {
                this.addClass("contains-removable");
            }
        }

        view.onClicked((event) => {
            if (new api.dom.ElementHelper(<HTMLElement>event.target).hasClass("remove")) {
                this.notifyItemRemoveClicked(item);
            } else {
                this.notifyItemClicked(item)
            }
        });

        if (!isContentSummaryValid(item)) {
            view.addClass("invalid");
            view.getEl().setTitle("Edit invalid content");
        }

        return view;
    }

    onItemClicked(listener: (item: ContentSummaryAndCompareStatus) => void) {
        this.itemClickListeners.push(listener);
    }

    unItemClicked(listener: (item: ContentSummaryAndCompareStatus) => void) {
        this.itemClickListeners = this.itemClickListeners.filter((curr) => {
            return curr !== listener;
        })
    }

    private notifyItemClicked(item) {
        this.itemClickListeners.forEach(listener => {
            listener(item);
        })
    }

    onItemRemoveClicked(listener: (item: ContentSummaryAndCompareStatus) => void) {
        this.removeClickListeners.push(listener);
    }

    unItemRemoveClicked(listener: (item: ContentSummaryAndCompareStatus) => void) {
        this.removeClickListeners = this.removeClickListeners.filter((curr) => {
            return curr !== listener;
        })
    }

    private notifyItemRemoveClicked(item) {
        this.removeClickListeners.forEach(listener => {
            listener(item);
        })
    }

}

function isContentSummaryValid(item: ContentSummaryAndCompareStatus): boolean {
    let status = item.getCompareStatus(),
        summary = item.getContentSummary();

    return status == CompareStatus.PENDING_DELETE ||
           (summary.isValid() && !api.util.StringHelper.isBlank(summary.getDisplayName()) && !summary.getName().isUnnamed());
}

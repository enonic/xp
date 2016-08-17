import "../../api.ts";
import {DependantItemsDialog, DialogDependantList} from "../dialog/DependantItemsDialog";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareContentResults = api.content.resource.result.CompareContentResults;
import DialogButton = api.ui.dialog.DialogButton;
import PublishContentRequest = api.content.resource.PublishContentRequest;
import ContentIds = api.content.ContentIds;
import ResolvePublishDependenciesResult = api.content.resource.result.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ContentId = api.content.ContentId;
import ListBox = api.ui.selector.list.ListBox;
import LoadMask = api.ui.mask.LoadMask;
import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;

/**
 * ContentPublishDialog manages list of initially checked (initially requested) items resolved via ResolvePublishDependencies command.
 * Resolved items are converted into array of SelectionPublishItem<ContentPublishItem> items and stored in selectionItems property.
 * Dependant items number will change depending on includeChildren checkbox state as
 * resolved dependencies usually differ in that case.
 */
export class ContentPublishDialog extends DependantItemsDialog {

    private childrenCheckbox: api.ui.Checkbox;

    private excludedIds: ContentId[] = [];

    private childrenLoaded: boolean = false;

    // stashes previous checkbox state items, until selected items changed
    private stash: {[checked:string]:ContentSummaryAndCompareStatus[]} = {};

    constructor() {
        super("Publishing Wizard", "Resolving items...", "Other items that will be published");

        this.setAutoUpdateTitle(false);
        this.getEl().addClass("publish-dialog");

        var publishAction = new ContentPublishDialogAction();
        publishAction.onExecuted(this.doPublish.bind(this));
        this.actionButton = this.addAction(publishAction, true, true);
        this.actionButton.setEnabled(false);

        this.addCancelButtonToBottom();

        this.initChildrenCheckbox();

        this.getItemList().onItemsRemoved((items: ContentSummaryAndCompareStatus[]) => {
            if (!this.isIgnoreItemsChanged()) {
                this.clearStashedItems();
                this.reloadPublishDependencies().done();
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
            this.clearStashedItems();
            this.reloadPublishDependencies().done();
        });

        return dependants;
    }

    open() {
        if (!this.doAnyHaveChildren(this.getItemList().getItems())) {

            this.childrenCheckbox.setVisible(false);
            this.getButtonRow().addClass("no-checkbox");
        }

        this.clearStashedItems();
        this.reloadPublishDependencies(true).done();

        this.excludedIds = [];
        this.childrenLoaded = false;

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

        var stashedItems = this.getStashedItems();
        // null - means items have not been loaded yet or we had to clear it because of selection change
        if (!stashedItems) {
            let childrenNotLoadedYet = this.childrenCheckbox.isChecked() && !this.childrenLoaded;
            return this.reloadPublishDependencies(childrenNotLoadedYet);
        } else {
            // apply the stash to avoid extra heavy request
            this.actionButton.setEnabled(false);
            this.loadMask.show();
            setTimeout(() => {
                this.setDependantItems(stashedItems.slice());
                this.centerMyself();
                this.actionButton.setEnabled(true);
                this.loadMask.hide();
            }, 100);
            return wemQ<void>(null);
        }
    }

    private reloadPublishDependencies(resetDependantItems?: boolean): wemQ.Promise<void> {
        this.actionButton.setEnabled(false);
        this.loadMask.show();
        this.disableCheckbox();

        this.setSubTitle("Resolving items...");

        let ids = this.getContentToPublishIds(),
            loadChildren = this.childrenCheckbox.isChecked();

        let resolveDependenciesRequest = api.content.resource.ResolvePublishDependenciesRequest.
            create().
            setIds(ids).
            setExcludedIds(this.excludedIds).
            setIncludeChildren(loadChildren).
            build();

        return resolveDependenciesRequest.sendAndParse().then((result: ResolvePublishDependenciesResult) => {

            this.toggleClass("contains-removable", result.isContainsRemovable());
            this.dependantIds = result.getDependants();
            return this.loadDescendants(0, 20).then((dependants: ContentSummaryAndCompareStatus[]) => {
                if (resetDependantItems) { // just opened or first time loading children
                    this.setDependantItems(dependants);
                }
                else {
                    this.filterDependantItems(dependants);
                }

                this.loadMask.hide();
                this.enableCheckbox();

                this.setStashedItems(dependants.slice());

                if (this.childrenCheckbox.isChecked()) {
                    this.childrenLoaded = true;
                }

                // do not set requested contents as they are never going to change,
                // but returned data contains less info than original summaries
                this.childrenCheckbox.setVisible(this.doAnyHaveChildren(this.getItemList().getItems()));

                this.centerMyself();
            });
        });
    }

    private filterDependantItems(dependants: ContentSummaryAndCompareStatus[]) {
        var itemsToRemove = this.getDependantList().getItems().filter(
            (oldDependantItem: ContentSummaryAndCompareStatus) => !dependants.some(
                (newDependantItem) => oldDependantItem.equals(newDependantItem)));
        this.getDependantList().removeItems(itemsToRemove);

        let count = this.countTotal();

        this.updateSubTitle(count);
        this.updateButtonCount("Publish", count);
    }


    setDependantItems(items: api.content.ContentSummaryAndCompareStatus[]) {
        super.setDependantItems(items);

        let count = this.countTotal();

        this.updateSubTitle(count);
        this.updateButtonCount("Publish", count);

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

    setIncludeChildItems(include: boolean, silent?: boolean) {
        this.childrenCheckbox.setChecked(include, silent);
        return this;
    }


    private initChildrenCheckbox() {

        let childrenCheckboxListener = () => this.refreshPublishDependencies().done();

        this.childrenCheckbox = api.ui.Checkbox.create().setLabelText('Include child items').build();
        this.childrenCheckbox.addClass('include-child-check');
        this.childrenCheckbox.onValueChanged(childrenCheckboxListener);

        this.overwriteDefaultArrows(this.childrenCheckbox);

        this.getButtonRow().appendChild(this.childrenCheckbox);
    }

    private getContentToPublishIds(): ContentId[] {
        return this.getItemList().getItems().map(item => {
            return item.getContentId();
        })
    }

    private extendsWindowHeightSize(): boolean {
        if (ResponsiveRanges._540_720.isFitOrBigger(this.getEl().getWidthWithBorder())) {
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

        this.showLoadingSpinner();
        this.actionButton.setEnabled(false);

        var selectedIds = this.getContentToPublishIds();

        new PublishContentRequest().setIncludeChildren(this.childrenCheckbox.isChecked())
            .setIds(selectedIds).
            setExcludedIds(this.excludedIds).send().then(
            (jsonResponse: api.rest.JsonResponse<api.content.json.PublishContentJson>) => {
                this.close();
                PublishContentRequest.feedback(jsonResponse);
            }).finally(() => {
                this.hideLoadingSpinner();
                this.actionButton.setEnabled(true);
            });
    }

    protected countTotal(): number {
        return this.countToPublish(this.getItemList().getItems())
               + this.dependantIds.length;
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

    protected updateButtonCount(actionString: string, count: number) {

        super.updateButtonCount(actionString, count);

        let canPublish = count > 0 && this.areItemsAndDependantsValid();

        this.actionButton.setEnabled(canPublish);
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

    private disableCheckbox() {
        this.childrenCheckbox.setDisabled(true);
        this.childrenCheckbox.addClass("disabled")
    }

    private enableCheckbox() {
        this.childrenCheckbox.setDisabled(false);
        this.childrenCheckbox.removeClass("disabled");
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

        if (CompareStatus.NEWER == item.getCompareStatus()) {
            view.addClass("removable");
            this.toggleClass("contains-removable", true);
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

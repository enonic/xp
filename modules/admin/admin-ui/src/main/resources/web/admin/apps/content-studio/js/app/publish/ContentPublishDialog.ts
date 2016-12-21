import "../../api.ts";
import {ProgressBarDialog} from "../dialog/ProgressBarDialog";
import {PublishDialogDependantList, isContentSummaryValid} from "./PublishDialogDependantList";
import {ContentPublishPromptEvent} from "../browse/ContentPublishPromptEvent";
import {SchedulePublishDialog} from "./SchedulePublishDialog";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import PublishContentRequest = api.content.resource.PublishContentRequest;
import ResolvePublishDependenciesResult = api.content.resource.result.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ContentId = api.content.ContentId;
import ListBox = api.ui.selector.list.ListBox;
import LoadMask = api.ui.mask.LoadMask;

/**
 * ContentPublishDialog manages list of initially checked (initially requested) items resolved via ResolvePublishDependencies command.
 * Resolved items are converted into array of SelectionPublishItem<ContentPublishItem> items and stored in selectionItems property.
 * Dependant items number will change depending on includeChildren checkbox state as
 * resolved dependencies usually differ in that case.
 */
export class ContentPublishDialog extends ProgressBarDialog {

    private childrenCheckbox: api.ui.Checkbox;

    private excludedIds: ContentId[] = [];

    private childrenLoaded: boolean = false;

    private contentFormsAreValid: boolean = false; // result of back-end check for all the publish candidates that do not pend deletion

    // stashes previous checkbox state items, until selected items changed
    private stash: {[checked: string]: ContentSummaryAndCompareStatus[]} = {};
    private stashedCount: {[checked: string]: number} = {};

    private scheduleDialog: SchedulePublishDialog;
    protected showScheduleDialogButton: api.ui.dialog.DialogButton;

    constructor() {
        super(
            "Publishing Wizard",
            "Resolving items...",
            "Other items that will be published",
            "is-publishing",
            () => {
                new ContentPublishPromptEvent([]).fire();
            }
        );

        this.setAutoUpdateTitle(false);
        this.getEl().addClass("publish-dialog");

        this.initShowScheduleAction();
        this.initPublishAction();
        this.addCancelButtonToBottom();

        this.initChildrenCheckbox();

        this.getItemList().onItemsRemoved((items: ContentSummaryAndCompareStatus[]) => {
            if (!this.isIgnoreItemsChanged()) {
                this.clearStashedItems();
                this.reloadPublishDependencies().done();
            }
        });
    }

    private initPublishAction() {
        var publishAction = new ContentPublishDialogAction();
        publishAction.onExecuted(this.doPublish.bind(this, false));
        this.actionButton = this.addAction(publishAction, true);
        this.lockControls();
    }

    private initShowScheduleAction() {
        var showScheduleAction = new ShowSchedulePublishDialogAction();
        showScheduleAction.onExecuted(this.showScheduleDialog.bind(this));
        this.showScheduleDialogButton = this.addAction(showScheduleAction, false);
        this.showScheduleDialogButton.setTitle("Schedule Publishing");
        this.showScheduleDialogButton.setEnabled(true);
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

    close() {
        super.close();
        this.childrenCheckbox.setChecked(false);
    }


    private getStashedItems(): ContentSummaryAndCompareStatus[] {
        return this.stash[String(this.childrenCheckbox.isChecked())];
    }

    private setStashedItems(items: ContentSummaryAndCompareStatus[]) {
        this.stash[String(this.childrenCheckbox.isChecked())] = items.slice();
    }

    private setStashedCount(count: number) {
        this.stashedCount[String(this.childrenCheckbox.isChecked())] = count;
    }

    private getStashedCount(): number {
        return this.stashedCount[String(this.childrenCheckbox.isChecked())];
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
            this.lockControls();
            this.loadMask.show();
            setTimeout(() => {
                this.setDependantItems(stashedItems.slice());
                this.centerMyself();
                this.updateSubTitleShowScheduleAndButtonCount();
                this.loadMask.hide();
            }, 100);
            return wemQ<void>(null);
        }
    }

    private reloadPublishDependencies(resetDependantItems?: boolean): wemQ.Promise<void> {
        if (this.isProgressBarEnabled()) {
            return wemQ<void>(null);
        }
        this.lockControls();
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
            this.contentFormsAreValid = result.areAllContentsValid();
            this.setStashedCount(this.dependantIds.length);
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
                this.updateSubTitleShowScheduleAndButtonCount();

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
        if (this.isProgressBarEnabled()) {
            return;
        }
        var itemsToRemove = this.getDependantList().getItems().filter(
            (oldDependantItem: ContentSummaryAndCompareStatus) => !dependants.some(
                (newDependantItem) => oldDependantItem.equals(newDependantItem)));
        this.getDependantList().removeItems(itemsToRemove);

        this.updateSubTitleShowScheduleAndButtonCount();
    }

    private updateSubTitleShowScheduleAndButtonCount() {
        let count = this.countTotal();

        this.updateSubTitle(count);
        this.updateShowScheduleDialogButton();
        this.updateButtonCount("Publish", count);
    }

    setDependantItems(items: api.content.ContentSummaryAndCompareStatus[]) {
        if (this.isProgressBarEnabled()) {
            return;
        }
        super.setDependantItems(items);

        if (this.getStashedItems()) {
            this.updateSubTitleShowScheduleAndButtonCount();
        }
    }

    setContentToPublish(contents: ContentSummaryAndCompareStatus[]) {
        if (this.isProgressBarEnabled()) {
            return this;
        }
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

    private showScheduleDialog() {
        if (!this.scheduleDialog) {
            this.scheduleDialog = new SchedulePublishDialog();
            this.scheduleDialog.onClose(() => {
                this.removeClass("masked");
                this.getEl().focus();
            });
            this.scheduleDialog.onSchedule(() => {
                this.doPublish(true);
            });
            this.addClickIgnoredElement(this.scheduleDialog);
        }
        this.scheduleDialog.open();
        this.addClass("masked");
    }

    private doPublish(scheduled: boolean = false) {

        this.lockControls();

        this.setSubTitle(this.countTotal() + " items are being published...");

        var selectedIds = this.getContentToPublishIds();

        var publishRequest = new PublishContentRequest()
            .setIncludeChildren(this.childrenCheckbox.isChecked())
            .setIds(selectedIds)
            .setExcludedIds(this.excludedIds);

        if (scheduled) {
            publishRequest.setPublishFrom(this.scheduleDialog.getFromDate());
            publishRequest.setPublishTo(this.scheduleDialog.getToDate());
        }

        publishRequest.sendAndParse().then((taskId: api.task.TaskId) => {
            this.pollTask(taskId);
        }).catch((reason) => {
            this.unlockControls();
            this.close();
            if (reason && reason.message) {
                api.notify.showError(reason.message);
            }
        });
    }

    protected countTotal(): number {
        return this.countToPublish(this.getItemList().getItems()) + this.getStashedCount();
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

        this.togglePublish(canPublish);
        if (canPublish) {
            this.getButtonRow().focusDefaultAction();
            this.updateTabbable();
        }
    }

    protected updateShowScheduleDialogButton() {
        if (this.areSomeItemsOffline()) {
            this.showScheduleDialogButton.show();
        } else {
            this.showScheduleDialogButton.hide();
        }
    }

    private areSomeItemsOffline(): boolean {
        let summaries: ContentSummaryAndCompareStatus[] = this.getItemList().getItems();
        return summaries.some((summary) => summary.getCompareStatus() === CompareStatus.NEW);
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
        return this.contentFormsAreValid &&
               this.areAllValid(this.getItemList().getItems()) &&
               this.areAllValid(this.getDependantList().getItems());
    }

    private disableCheckbox() {
        this.childrenCheckbox.setDisabled(true);
        this.childrenCheckbox.addClass("disabled")
    }

    private enableCheckbox() {
        this.childrenCheckbox.setDisabled(false);
        this.childrenCheckbox.removeClass("disabled");
    }

    protected hasSubDialog(): boolean {
        return true;
    }

    private togglePublish(enable) {
        this.toggleControls(enable);
        this.toggleClass('no-publish', !enable);
    }
}

export class ContentPublishDialogAction extends api.ui.Action {
    constructor() {
        super("Publish");
        this.setIconClass("publish-action");
    }
}

export class ShowSchedulePublishDialogAction extends api.ui.Action {
    constructor() {
        super();
        this.setIconClass("show-schedule-action");
    }
}

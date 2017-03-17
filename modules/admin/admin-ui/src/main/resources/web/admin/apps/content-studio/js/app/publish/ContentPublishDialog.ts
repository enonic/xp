import '../../api.ts';
import {ProgressBarDialog} from '../dialog/ProgressBarDialog';
import {PublishDialogDependantList, isContentSummaryValid} from './PublishDialogDependantList';
import {ContentPublishPromptEvent} from '../browse/ContentPublishPromptEvent';
import {SchedulePublishDialog} from './SchedulePublishDialog';
import {PublishDialogItemList} from './PublishDialogItemList';

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import PublishContentRequest = api.content.resource.PublishContentRequest;
import ResolvePublishDependenciesResult = api.content.resource.result.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ContentId = api.content.ContentId;
import ListBox = api.ui.selector.list.ListBox;
import LoadMask = api.ui.mask.LoadMask;
import BrowseItem = api.app.browse.BrowseItem;
import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;
import Checkbox = api.ui.Checkbox;

/**
 * ContentPublishDialog manages list of initially checked (initially requested) items resolved via ResolvePublishDependencies command.
 * Resolved items are converted into array of SelectionPublishItem<ContentPublishItem> items and stored in selectionItems property.
 * Dependant items number will change depending on includeChildren checkbox state as
 * resolved dependencies usually differ in that case.
 */
export class ContentPublishDialog extends ProgressBarDialog {

    private excludedIds: ContentId[] = [];

    private containsInvalid: boolean;

    private includeOfflineCheckbox: Checkbox;

    private scheduleDialog: SchedulePublishDialog;

    protected showScheduleDialogButton: api.ui.dialog.DialogButton;

    constructor() {
        super(
            'Publishing Wizard',
            'Resolving items...',
            'Other items that will be published',
            'is-publishing',
            () => {
                new ContentPublishPromptEvent([]).fire();
            }
        );

        this.setAutoUpdateTitle(false);
        this.getEl().addClass('publish-dialog');

        this.initIncludeOfflineCheckbox();
        this.initActions();
        this.addCancelButtonToBottom();

        this.getItemList().onItemsRemoved((items: ContentSummaryAndCompareStatus[]) => {
            if (!this.isIgnoreItemsChanged()) {
                this.reloadPublishDependencies().done();
            }
        });

        this.getItemList().onExcludeChildrenListChanged((excludedChildrenIds: ContentId[]) => {
            this.reloadPublishDependencies(true).done();
        });
    }

    private initIncludeOfflineCheckbox() {
        let childrenCheckboxListener = () => this.reloadPublishDependencies(true).done();

        this.includeOfflineCheckbox = api.ui.Checkbox.create().setLabelText('Include offline items').build();
        this.includeOfflineCheckbox.addClass('include-offline');
        this.includeOfflineCheckbox.onValueChanged(childrenCheckboxListener);
        this.includeOfflineCheckbox.setVisible(false);

        this.getButtonRow().appendChild(this.includeOfflineCheckbox);
    }

    private initActions() {
        let showScheduleAction = new ShowSchedulePublishDialogAction();
        showScheduleAction.onExecuted(this.showScheduleDialog.bind(this));
        this.showScheduleDialogButton = this.addAction(showScheduleAction, false);
        this.showScheduleDialogButton.setTitle('Schedule Publishing');

        let publishAction = new ContentPublishDialogAction();
        publishAction.onExecuted(this.doPublish.bind(this, false));
        this.actionButton = this.addAction(publishAction, true);

        this.lockControls();
    }

    protected createDependantList(): PublishDialogDependantList {
        let dependants = new PublishDialogDependantList();

        dependants.onItemClicked((item: ContentSummaryAndCompareStatus) => {
            if (!isContentSummaryValid(item)) {
                new api.content.event.EditContentEvent([item]).fire();
                this.close();
            }
        });

        dependants.onItemRemoveClicked((item: ContentSummaryAndCompareStatus) => {
            this.excludedIds.push(item.getContentId());
            this.reloadPublishDependencies(true).done();
        });

        return dependants;
    }

    protected getDependantList(): PublishDialogDependantList {
        return <PublishDialogDependantList>super.getDependantList();
    }

    open() {
        this.excludedIds = [];

        this.reloadPublishDependencies(true).done();

        super.open();
    }

    close() {
        super.close();
        this.getItemList().clearExcludeChildrenIds();
    }

    protected lockControls() {
        super.lockControls();
        this.showScheduleDialogButton.setEnabled(false);
    }

    protected unlockControls() {
        super.unlockControls();
        this.showScheduleDialogButton.setEnabled(true);
    }

    private reloadPublishDependencies(resetDependantItems?: boolean): wemQ.Promise<void> {
        if (this.isProgressBarEnabled()) {
            return wemQ<void>(null);
        }
        this.lockControls();
        this.loadMask.show();

        this.setSubTitle('Resolving items...');

        let ids = this.getContentToPublishIds();

        let resolveDependenciesRequest = api.content.resource.ResolvePublishDependenciesRequest.create().
            setIds(ids).
            setExcludedIds(this.excludedIds).
            setExcludeChildrenIds(this.getItemList().getExcludeChildrenIds()).
            setIncludeOffline(this.includeOfflineCheckbox.isChecked()).
            build();

        return resolveDependenciesRequest.sendAndParse().then((result: ResolvePublishDependenciesResult) => {

            this.dependantIds = result.getDependants().slice();

            this.getDependantList().setRequiredIds(result.getRequired());

            this.containsInvalid = result.isContainsInvalid();

            this.includeOfflineCheckbox.setVisible(
                this.getItemList().allIncludeChildrenTogglersAreEnabled() && this.dependantIds.length > 0
            );

            return this.loadDescendants(0, 20).then((dependants: ContentSummaryAndCompareStatus[]) => {
                if (resetDependantItems) { // just opened or first time loading children
                    this.setDependantItems(dependants);
                } else {
                    this.filterDependantItems(dependants);
                }

                this.loadMask.hide();
                this.updateSubTitleShowScheduleAndButtonCount();

                this.centerMyself();
            });
        });
    }

    private filterDependantItems(dependants: ContentSummaryAndCompareStatus[]) {
        if (this.isProgressBarEnabled()) {
            return;
        }
        let itemsToRemove = this.getDependantList().getItems().filter(
            (oldDependantItem: ContentSummaryAndCompareStatus) => !dependants.some(
                (newDependantItem) => oldDependantItem.equals(newDependantItem)));
        this.getDependantList().removeItems(itemsToRemove);

        this.updateSubTitleShowScheduleAndButtonCount();
    }

    private updateSubTitleShowScheduleAndButtonCount() {
        let count = this.countTotal();

        this.updateSubTitle(count);
        this.updateShowScheduleDialogButton();
        this.updateButtonCount('Publish', count);
    }

    setDependantItems(items: api.content.ContentSummaryAndCompareStatus[]) {
        if (this.isProgressBarEnabled()) {
            return;
        }
        super.setDependantItems(items);
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
        this.getItemList().getItemViews()
            .filter(itemView => itemView.getIncludeChildrenToggler())
            .forEach(itemView => itemView.getIncludeChildrenToggler().toggle(include, silent)
            );
        return this;
    }

    private getContentToPublishIds(): ContentId[] {
        return this.getItemList().getItems().map(item => {
            return item.getContentId();
        });
    }

    private showScheduleDialog() {
        if (!this.scheduleDialog) {
            this.scheduleDialog = new SchedulePublishDialog();
            this.scheduleDialog.onClose(() => {
                this.removeClass('masked');
                this.getEl().focus();
            });
            this.scheduleDialog.onSchedule(() => {
                this.doPublish(true);
            });
            this.addClickIgnoredElement(this.scheduleDialog);
        }
        this.scheduleDialog.open();
        this.addClass('masked');
    }

    private doPublish(scheduled: boolean = false) {

        this.lockControls();

        this.setSubTitle(this.countTotal() + ' items are being published...');

        let selectedIds = this.getContentToPublishIds();

        let publishRequest = new PublishContentRequest()
            .setIds(selectedIds)
            .setExcludedIds(this.excludedIds)
            .setExcludeChildrenIds(this.getItemList().getExcludeChildrenIds());

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

    protected createItemList(): ListBox<ContentSummaryAndCompareStatus> {
        return new PublishDialogItemList();
    }

    protected getItemList(): PublishDialogItemList {
        return <PublishDialogItemList>super.getItemList();
    }

    protected countTotal(): number {
        return this.countToPublish(this.getItemList().getItems()) + this.getDependantIds().length;
    }

    private countToPublish(summaries: ContentSummaryAndCompareStatus[]): number {
        return summaries.reduce((count, summary: ContentSummaryAndCompareStatus) => {
            return summary.getCompareStatus() !== CompareStatus.EQUAL ? ++count : count;
        }, 0);
    }

    private updateSubTitle(count: number) {
        let allValid = this.areItemsAndDependantsValid();

        let subTitle = count === 0
            ? 'No items to publish'
            : allValid ? 'Your changes are ready for publishing'
                           : 'Invalid item(s) prevent publish';

        this.setSubTitle(subTitle);
        this.toggleClass('invalid', !allValid);
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

    private areItemsAndDependantsValid(): boolean {
        return !this.containsInvalid;
    }

    protected hasSubDialog(): boolean {
        return true;
    }

    private togglePublish(enable: boolean) {
        this.toggleControls(enable);
        this.toggleClass('no-publish', !enable);
    }
}

export class ContentPublishDialogAction extends api.ui.Action {
    constructor() {
        super('Publish');
        this.setIconClass('publish-action');
    }
}

export class ShowSchedulePublishDialogAction extends api.ui.Action {
    constructor() {
        super();
        this.setIconClass('show-schedule-action');
    }
}

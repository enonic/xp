import '../../api.ts';
import {PublishDialogDependantList, isContentSummaryValid} from './PublishDialogDependantList';
import {ContentPublishPromptEvent} from '../browse/ContentPublishPromptEvent';
import {PublishDialogItemList} from './PublishDialogItemList';
import {CreateIssueDialog} from './CreateIssueDialog';
import {SchedulableDialog} from '../dialog/SchedulableDialog';

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import PublishContentRequest = api.content.resource.PublishContentRequest;
import ResolvePublishDependenciesResult = api.content.resource.result.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ContentId = api.content.ContentId;
import ListBox = api.ui.selector.list.ListBox;
import LoadMask = api.ui.mask.LoadMask;
import BrowseItem = api.app.browse.BrowseItem;
import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;

/**
 * ContentPublishDialog manages list of initially checked (initially requested) items resolved via ResolvePublishDependencies command.
 * Resolved items are converted into array of SelectionPublishItem<ContentPublishItem> items and stored in selectionItems property.
 * Dependant items number will change depending on includeChildren checkbox state as
 * resolved dependencies usually differ in that case.
 */
export class ContentPublishDialog extends SchedulableDialog {

    private excludedIds: ContentId[] = [];

    private containsInvalid: boolean;

    private allPublishable: boolean;

    private createIssueDialog: CreateIssueDialog;

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

    protected initActions() {
        super.initActions();
        this.setButtonAction(ContentPublishDialogAction, this.doPublish.bind(this, false));

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

        if (this.createIssueDialog) {
            this.createIssueDialog.reset();
        }

        this.reloadPublishDependencies(true).done();

        super.open();
    }

    close() {
        super.close();
        this.getItemList().clearExcludeChildrenIds();
    }

    private reloadPublishDependencies(resetDependantItems?: boolean): wemQ.Promise<void> {
        if (this.isProgressBarEnabled()) {
            return wemQ<void>(null);
        }
        this.lockControls();
        this.loadMask.show();

        this.setSubTitle('Resolving items...');

        let ids = this.getContentToPublishIds();

        let resolveDependenciesRequest = api.content.resource.ResolvePublishDependenciesRequest.create().setIds(ids).setExcludedIds(
            this.excludedIds).setExcludeChildrenIds(this.getItemList().getExcludeChildrenIds()).build();

        return resolveDependenciesRequest.sendAndParse().then((result: ResolvePublishDependenciesResult) => {

            this.dependantIds = result.getDependants().slice();

            this.getDependantList().setRequiredIds(result.getRequired());

            this.containsInvalid = result.isContainsInvalid();
            this.allPublishable = result.isAllPublishable();

            this.updateButtonAction();

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
        this.updateButtonCount(this.allPublishable ? 'Publish' : 'Create Issue... ', count);
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

    private showCreateIssueDialog() {
        if (!this.createIssueDialog) {
            this.createIssueDialog = CreateIssueDialog.get();

            this.createIssueDialog.onClosed(() => {
                this.removeClass('masked');
                this.getEl().focus();
            });

            this.createIssueDialog.onSucceed(() => {
                if(this.isVisible()) {
                    this.close();
                }
            });

            this.addClickIgnoredElement(this.createIssueDialog);
        }

        const idsToPublish = this.getContentToPublishIds();

        this.createIssueDialog.setItems(idsToPublish, this.getItemList().getExcludeChildrenIds());
        this.createIssueDialog.setExcludeIds(this.excludedIds);
        this.createIssueDialog.setFullContentCount(idsToPublish.length + this.dependantIds.length);

        this.createIssueDialog.open();

        this.addClass('masked');
    }

    private createIssue() {
        //TODO: implement action
        this.showCreateIssueDialog();
    }

    private setButtonAction(dialogActionClass: { new(): api.ui.Action }, listener: () => wemQ.Promise<any>|void) {
        if (!!this.actionButton && api.ObjectHelper.iFrameSafeInstanceOf(this.actionButton.getAction(), dialogActionClass)) {
            return;
        }

        if (this.actionButton) {
            this.removeAction(this.actionButton);
        }

        let newAction = new dialogActionClass();
        newAction.onExecuted(() => listener());

        this.actionButton = this.addAction(newAction, true);
    }

    private updateButtonAction() {
        if (this.allPublishable) {
            this.setButtonAction(ContentPublishDialogAction, this.doPublish.bind(this, false));
            this.updateDependantsHeader();
        } else {
            this.setButtonAction(CreateIssueDialogAction, this.createIssue.bind(this));
            this.updateDependantsHeader('Other items that will be added to the Publishing Issue');
        }
    }

    private doPublish(scheduled: boolean = false) {

        this.lockControls();

        this.setSubTitle(this.countTotal() + '' +
                         ' items are being published...');

        let selectedIds = this.getContentToPublishIds();

        let publishRequest = new PublishContentRequest()
            .setIds(selectedIds)
            .setExcludedIds(this.excludedIds)
            .setExcludeChildrenIds(this.getItemList().getExcludeChildrenIds());

        if (scheduled) {
            publishRequest.setPublishFrom(this.getFromDate());
            publishRequest.setPublishTo(this.getToDate());
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

    private updateSubTitle(count: number) {
        let allValid = this.areItemsAndDependantsValid();

        let subTitle = (count === 0) ?
                       'No items to publish' :
                       this.allPublishable ?
                       (allValid ? 'Your changes are ready for publishing' : 'Invalid item(s) prevent publishing') :
                       'Create a new Publishing Issue with selected item(s)';

        this.setSubTitle(subTitle);
        this.toggleClass('invalid', !allValid && this.allPublishable);
    }

    protected updateButtonCount(actionString: string, count: number) {
        super.updateButtonCount(actionString, count);

        let canPublish = count > 0 && this.areItemsAndDependantsValid();

        this.updateButtonStatus(canPublish);

        if (canPublish) {
            this.getButtonRow().focusDefaultAction();
            this.updateTabbable();
        }
    }

    protected updateButtonStatus(enabled: boolean) {
        if (api.ObjectHelper.iFrameSafeInstanceOf(this.actionButton.getAction(), ContentPublishDialogAction)) {
            this.toggleAction(enabled);
        } else {
            this.toggleAction(true);
        }
    }

    protected doScheduledAction() {
        this.doPublish(true);
    }

    protected isScheduleButtonAllowed(): boolean {

        return api.ObjectHelper.iFrameSafeInstanceOf(this.actionButton.getAction(), ContentPublishDialogAction) &&
               this.areSomeItemsOffline();
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
}

export class ContentPublishDialogAction extends api.ui.Action {
    constructor() {
        super('Publish');
        this.setIconClass('publish-action');
    }
}

export class CreateIssueDialogAction extends api.ui.Action {
    constructor() {
        super('Create Issue... ');
        this.setIconClass('create-issue-action');
    }
}

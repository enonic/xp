import '../../api.ts';
import {ProgressBarConfig} from '../dialog/ProgressBarDialog';
import {isContentSummaryValid, PublishDialogDependantList} from './PublishDialogDependantList';
import {ContentPublishPromptEvent} from '../browse/ContentPublishPromptEvent';
import {PublishDialogItemList} from './PublishDialogItemList';
import {CreateIssueDialog} from '../issue/view/CreateIssueDialog';
import {SchedulableDialog} from '../dialog/SchedulableDialog';
import {PublishProcessor} from './PublishProcessor';
import {IssueServerEventsHandler} from '../issue/event/IssueServerEventsHandler';
import {Issue} from '../issue/Issue';

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
import HasUnpublishedChildrenResult = api.content.resource.result.HasUnpublishedChildrenResult;
import HasUnpublishedChildrenRequest = api.content.resource.HasUnpublishedChildrenRequest;
import ModalDialogButtonRow = api.ui.dialog.ButtonRow;
import MenuButton = api.ui.button.MenuButton;
import Action = api.ui.Action;
import ActionButton = api.ui.button.ActionButton;
import User = api.security.User;
import DropdownButtonRow = api.ui.dialog.DropdownButtonRow;
import i18n = api.util.i18n;

/**
 * ContentPublishDialog manages list of initially checked (initially requested) items resolved via ResolvePublishDependencies command.
 * Resolved items are converted into array of SelectionPublishItem<ContentPublishItem> items and stored in selectionItems property.
 * Dependant items number will change depending on includeChildren checkbox state as
 * resolved dependencies usually differ in that case.
 */
export class ContentPublishDialog extends SchedulableDialog {

    private publishButton: ActionButton;

    private createIssueAction: Action;

    private publishProcessor: PublishProcessor;

    private currentUser: User;

    constructor() {
        super(<ProgressBarConfig> {
            dialogName: i18n('dialog.publish'),
            dialogSubName: i18n('dialog.publish.resolving'),
            dependantsName: i18n('dialog.publish.dependants'),
                isProcessingClass: 'is-publishing',
                processHandler: () => {
                    new ContentPublishPromptEvent([]).fire();
                },
                buttonRow: new ContentPublishDialogButtonRow(),
            }
        );

        this.publishProcessor = new PublishProcessor(this.getItemList(), this.getDependantList());

        this.publishProcessor.onLoadingStarted(() => {
            this.lockControls();
            this.loadMask.show();

            this.setSubTitle(i18n('dialog.publish.resolving'));
        });

        this.publishProcessor.onLoadingFinished(() => {
            this.updateButtonAction();

            const ids = this.getContentToPublishIds();

            new HasUnpublishedChildrenRequest(ids).sendAndParse().then((children) => {
                const toggleable = children.getResult().some(requestedResult => requestedResult.getHasChildren());
                this.getItemList().setContainsToggleable(toggleable);

                children.getResult().forEach((requestedResult) => {
                    const item = this.getItemList().getItemViewById(requestedResult.getId());

                    if (item) {
                        item.setTogglerActive(requestedResult.getHasChildren());
                    }
                });
            });

            this.loadMask.hide();
            this.updateSubTitleShowScheduleAndButtonCount();

            this.centerMyself();
        });

        this.getEl().addClass('publish-dialog');

        this.initActions();
        this.addCancelButtonToBottom();
        this.loadCurrentUser();
        this.handleIssueGlobalEvents();

        this.addClickIgnoredElement(CreateIssueDialog.get());
    }

    private loadCurrentUser() {
        return new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {
            this.currentUser = loginResult.getUser();
        });
    }

    private handleIssueGlobalEvents() {

        IssueServerEventsHandler.getInstance().onIssueCreated((issues: Issue[]) => {
            if (this.isVisible()) {
                if (issues.some((issue) => this.isIssueCreatedByCurrentUser(issue))) {
                    this.close();
                }
            }
        });
    }

    isIssueCreatedByCurrentUser(issue: Issue): boolean {
        if (!issue.getCreator()) {
            return false;
        }

        return issue.getCreator() === this.currentUser.getKey().toString();
    }

    protected initActions() {
        super.initActions();

        const publishAction = new ContentPublishDialogAction(this.doPublish.bind(this, false));
        this.createIssueAction = new CreateIssueDialogAction(this.createIssue.bind(this));

        const actionMenu: MenuButton = this.getButtonRow().makeActionMenu(publishAction, [this.createIssueAction, this.showScheduleAction]);

        this.actionButton = actionMenu.getActionButton();
        this.publishButton = actionMenu.getActionButton();

        this.updatePublishableStatus();

        this.lockControls();
    }

    protected createDependantList(): PublishDialogDependantList {
        let dependants = new PublishDialogDependantList();

        dependants.onItemClicked((item: ContentSummaryAndCompareStatus) => {
            if (!isContentSummaryValid(item)) {
                this.close();
            }
        });

        return dependants;
    }

    protected getDependantList(): PublishDialogDependantList {
        return <PublishDialogDependantList>super.getDependantList();
    }

    getButtonRow(): ContentPublishDialogButtonRow {
        return <ContentPublishDialogButtonRow>super.getButtonRow();
    }

    open() {
        this.publishProcessor.resetExcludedIds();

        CreateIssueDialog.get().reset();

        this.reloadPublishDependencies(true).done();

        super.open();
    }

    close() {
        super.close();
        this.getItemList().clearExcludeChildrenIds();

        CreateIssueDialog.get().reset();
    }

    private updateSubTitleShowScheduleAndButtonCount() {
        let count = this.countTotal();

        this.updateSubTitle(count);
        this.updateShowScheduleDialogButton();
        this.updateButtonCount(null, count);
    }

    protected countTotal(): number {
        return this.publishProcessor.countTotal();
    }

    protected getDependantIds(): ContentId[] {
        return this.publishProcessor.getDependantIds();
    }

    protected setIgnoreItemsChanged(value: boolean) {
        super.setIgnoreItemsChanged(value);
        this.publishProcessor.setIgnoreItemsChanged(value);
    }

    public getContentToPublishIds(): ContentId[] {
        return this.publishProcessor.getContentToPublishIds();
    }

    public getExcludedIds(): ContentId[] {
        return this.publishProcessor.getExcludedIds();
    }

    public isAllPublishable(): boolean {
        return this.publishProcessor.isAllPublishable();
    }

    public isContainsInvalid(): boolean {
        return this.publishProcessor.isContainsInvalid();
    }

    private reloadPublishDependencies(resetDependantItems?: boolean): wemQ.Promise<void> {
        if (this.isProgressBarEnabled()) {
            return wemQ<void>(null);
        }
        return this.publishProcessor.reloadPublishDependencies(resetDependantItems);

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

    private showCreateIssueDialog() {
        let createIssueDialog = CreateIssueDialog.get();

        createIssueDialog.enableBackButton();
        createIssueDialog.setItems(this.getItemList().getItems()/*idsToPublish, this.getItemList().getExcludeChildrenIds()*/);
        createIssueDialog.setExcludedIds(this.getExcludedIds());
        createIssueDialog.setExcludeChildrenIds(this.getItemList().getExcludeChildrenIds());

        createIssueDialog.lockPublishItems();
        createIssueDialog.open(this);

        this.addClass('masked');
    }

    private createIssue() {
        //TODO: implement action
        this.showCreateIssueDialog();
    }

    private updatePublishableStatus() {
        this.toggleClass('publishable', this.isAllPublishable());
    }

    private updateButtonAction() {
        const header = this.isAllPublishable() ? null : i18n('dialog.publish.dependantsIssue');
        this.updateDependantsHeader(header);

        this.updatePublishableStatus();
    }

    private doPublish(scheduled: boolean = false) {

        this.lockControls();

        this.setSubTitle(i18n('dialog.publish.publishing', this.countTotal()));

        let selectedIds = this.getContentToPublishIds();

        let publishRequest = new PublishContentRequest()
            .setIds(selectedIds)
            .setExcludedIds(this.getExcludedIds())
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
        const allValid = this.areItemsAndDependantsValid();

        let subTitle = (count === 0) ? i18n('dialog.publish.noItems') :
                       this.isAllPublishable() ?
                       (allValid ? i18n('dialog.publish.changesReady') : i18n('dialog.publish.invalidError')
                       ) : i18n('dialog.publish.newIssue');

        this.setSubTitle(subTitle);
        this.toggleClass('invalid', !allValid && this.isAllPublishable());
    }

    protected updateButtonCount(actionString: string, count: number) {
        const canPublish = count > 0 && this.areItemsAndDependantsValid();

        this.updateButtonStatus(canPublish);

        if (canPublish) {
            this.getButtonRow().focusDefaultAction();
            this.updateTabbable();
        }

        const labelWithNumber = (num, label) => `${label}${num > 1 ? ` (${num})` : '' }`;

        this.publishButton.getAction().setLabel(labelWithNumber(count, i18n('action.publish')));

        this.showScheduleAction.setLabel(labelWithNumber(count, i18n('action.scheduleMore')));
        this.createIssueAction.setLabel(labelWithNumber(this.getItemList().getItemCount(), i18n('action.createIssueMore')));
    }

    protected updateButtonStatus(enabled: boolean) {
        this.toggleAction(!this.isAllPublishable() || enabled);
    }

    protected doScheduledAction() {
        this.doPublish(true);
    }

    protected isScheduleButtonAllowed(): boolean {
        return this.isAllPublishable() && this.areSomeItemsOffline();
    }

    private areSomeItemsOffline(): boolean {
        let summaries: ContentSummaryAndCompareStatus[] = this.getItemList().getItems();
        return summaries.some((summary) => summary.getCompareStatus() === CompareStatus.NEW);
    }

    private areItemsAndDependantsValid(): boolean {
        return !this.isContainsInvalid();
    }

    protected hasSubDialog(): boolean {
        return true;
    }

    protected lockControls() {
        super.lockControls();
        this.getButtonRow().getActionMenu().setEnabled(false);
    }

    protected unlockControls() {
        super.unlockControls();
        this.getButtonRow().getActionMenu().setEnabled(true);
    }
}

export class ContentPublishDialogButtonRow extends DropdownButtonRow {

    makeActionMenu(mainAction: Action, menuActions: Action[], useDefault: boolean = true): MenuButton {
        super.makeActionMenu(mainAction, menuActions, useDefault);

        return <MenuButton>this.actionMenu.addClass('publish-dialog-menu');
    }

}

export class ContentPublishDialogAction extends api.ui.Action {
    constructor(handler: () => wemQ.Promise<any>|void) {
        super(i18n('action.publish'));
        this.setIconClass('publish-action');
        this.onExecuted(handler);
    }
}

export class CreateIssueDialogAction extends api.ui.Action {
    constructor(handler: () => wemQ.Promise<any>|void) {
        super(i18n('action.createIssueMore'));
        this.setIconClass('create-issue-action');
        this.onExecuted(handler);
    }
}

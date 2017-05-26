import '../../api.ts';
import {ProgressBarConfig} from '../dialog/ProgressBarDialog';
import {PublishDialogDependantList, isContentSummaryValid} from './PublishDialogDependantList';
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
import ModalDialogButtonRow = api.ui.dialog.ModalDialogButtonRow;
import MenuButton = api.ui.button.MenuButton;
import Action = api.ui.Action;
import ActionButton = api.ui.button.ActionButton;
import User = api.security.User;

/**
 * ContentPublishDialog manages list of initially checked (initially requested) items resolved via ResolvePublishDependencies command.
 * Resolved items are converted into array of SelectionPublishItem<ContentPublishItem> items and stored in selectionItems property.
 * Dependant items number will change depending on includeChildren checkbox state as
 * resolved dependencies usually differ in that case.
 */
export class ContentPublishDialog extends SchedulableDialog {

    private createIssueDialog: CreateIssueDialog;

    private publishButton: ActionButton;

    private createIssueButton: ActionButton;

    private publishProcessor: PublishProcessor;

    private currentUser: User;

    constructor() {
        super(<ProgressBarConfig> {
                dialogName: 'Publishing Wizard',
                dialogSubName: 'Resolving items...',
                dependantsName: 'Other items that will be published',
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

            this.setSubTitle('Resolving items...');
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

        this.setAutoUpdateTitle(false);
        this.getEl().addClass('publish-dialog');

        this.initActions();
        this.addCancelButtonToBottom();
        this.loadCurrentUser();
        this.handleIssueGlobalEvents();
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
        const createIssueAction = new CreateIssueDialogAction(this.createIssue.bind(this));

        const actionMenu: MenuButton = this.getButtonRow().makeActionMenu(publishAction, [createIssueAction]);
        this.createIssueButton = this.getButtonRow().addAction(createIssueAction);

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
        return this.publishProcessor.reloadPublishDependencies(resetDependantItems).then(() => {

        });

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
        if (!this.createIssueDialog) {
            this.createIssueDialog = CreateIssueDialog.get();

            this.createIssueDialog.onClosed(() => {
                this.removeClass('masked');
                this.getEl().focus();
            });

            this.addClickIgnoredElement(this.createIssueDialog);
        }

        this.createIssueDialog.setItems(this.getItemList().getItems()/*idsToPublish, this.getItemList().getExcludeChildrenIds()*/);
        this.createIssueDialog.setExcludedIds(this.getExcludedIds());
        this.createIssueDialog.setExcludeChildrenIds(this.getItemList().getExcludeChildrenIds());

        this.createIssueDialog.lockPublishItems();
        this.createIssueDialog.open();

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
        const header = this.isAllPublishable() ? null : 'Other items that will be added to the Publishing Issue';
        this.updateDependantsHeader(header);

        const defaultFocusElement = this.isAllPublishable()
            ? this.getButtonRow().getActionMenu().getActionButton()
            : this.createIssueButton;
        this.getButtonRow().setDefaultElement(defaultFocusElement);

        this.updatePublishableStatus();
    }

    private doPublish(scheduled: boolean = false) {

        this.lockControls();

        this.setSubTitle(`${this.countTotal()} items are being published...`);

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

        let subTitle = (count === 0) ?
                       'No items to publish' :
                       this.isAllPublishable() ?
                       (allValid ?
                        'Your changes are ready for publishing' :
                        'Invalid item(s) prevent publishing'
                       ) :
                       'Create a new Publishing Issue with selected item(s)';

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

        this.publishButton.getAction().setLabel(labelWithNumber(count, 'Publish'));
        this.createIssueButton.getAction().setLabel(labelWithNumber(count, 'Create Issue... '));
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
        this.createIssueButton.setEnabled(false);
    }

    protected unlockControls() {
        super.unlockControls();
        this.getButtonRow().getActionMenu().setEnabled(true);
        this.createIssueButton.setEnabled(true);
    }
}

export class ContentPublishDialogButtonRow extends ModalDialogButtonRow {

    private actionMenu: MenuButton;

    makeActionMenu(mainAction: Action, menuActions: Action[], useDefault: boolean = true): MenuButton {
        if (!this.actionMenu) {
            this.actionMenu = new MenuButton(mainAction, menuActions);

            if (useDefault) {
                this.setDefaultElement(this.actionMenu);
            }

            this.actionMenu.addClass('publish-dialog-menu');
            this.actionMenu.getDropdownHandle().addClass('no-animation');
            this.addElement(this.actionMenu);
        }

        return this.actionMenu;
    }

    getActionMenu(): MenuButton {
        return this.actionMenu;
    }
}

export class ContentPublishDialogAction extends api.ui.Action {
    constructor(handler: () => wemQ.Promise<any>|void) {
        super('Publish');
        this.setIconClass('publish-action');
        this.onExecuted(handler);
    }
}

export class CreateIssueDialogAction extends api.ui.Action {
    constructor(handler: () => wemQ.Promise<any>|void) {
        super('Create Issue... ');
        this.setIconClass('create-issue-action');
        this.onExecuted(handler);
    }
}

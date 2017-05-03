import '../../api.ts';
import {ProgressBarConfig} from '../dialog/ProgressBarDialog';
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
import ModalDialogButtonRow = api.ui.dialog.ModalDialogButtonRow;
import MenuButton = api.ui.button.MenuButton;
import PublishableStatus = api.content.PublishableStatus;
import Action = api.ui.Action;
import ActionButton = api.ui.button.ActionButton;

/**
 * ContentPublishDialog manages list of initially checked (initially requested) items resolved via ResolvePublishDependencies command.
 * Resolved items are converted into array of SelectionPublishItem<ContentPublishItem> items and stored in selectionItems property.
 * Dependant items number will change depending on includeChildren checkbox state as
 * resolved dependencies usually differ in that case.
 */
export class ContentPublishDialog extends SchedulableDialog {

    private excludedIds: ContentId[] = [];

    private containsInvalid: boolean;

    private publishableStatus: PublishableStatus;

    private createIssueDialog: CreateIssueDialog;

    private publishButton: ActionButton;

    private createIssueButton: ActionButton;

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

        this.setAutoUpdateTitle(false);
        this.getEl().addClass('publish-dialog');

        this.initActions();
        this.addCancelButtonToBottom();

        this.getItemList().onItemsRemoved(() => {
            if (!this.isIgnoreItemsChanged()) {
                this.reloadPublishDependencies().done();
            }
        });

        this.getItemList().onExcludeChildrenListChanged(() => {
            this.reloadPublishDependencies(true).done();
        });
    }

    protected initActions() {
        super.initActions();

        const publishAction = new ContentPublishDialogAction(this.doPublish.bind(this, false));
        const createIssueAction = new CreateIssueDialogAction(this.createIssue.bind(this));

        const actionMenu: MenuButton = this.getButtonRow().makeActionMenu(publishAction, [createIssueAction]);
        this.createIssueButton = this.getButtonRow().addAction(createIssueAction);

        this.actionButton = actionMenu.getActionButton();
        this.publishButton = actionMenu.getActionButton();

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

    getButtonRow(): ContentPublishDialogButtonRow {
        return <ContentPublishDialogButtonRow>super.getButtonRow();
    }

    private isAllPublishable(): boolean {
        return this.publishableStatus === PublishableStatus.ALL;
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
            this.publishableStatus = result.getPublishableStatus();

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
        this.updateButtonCount(null, count);
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

    private updateButtonAction() {
        const allPublishable = this.isAllPublishable();
        const header = allPublishable ? null : 'Other items that will be added to the Publishing Issue';
        this.updateDependantsHeader(header);

        const defaultFocusElement = allPublishable ? this.getButtonRow().getActionMenu().getActionButton() : this.createIssueButton;
        this.getButtonRow().setDefaultElement(defaultFocusElement);

        this.publishButton.setVisible(allPublishable);
        this.createIssueButton.setVisible(!allPublishable);
    }

    private doPublish(scheduled: boolean = false) {

        this.lockControls();

        this.setSubTitle(`${this.countTotal()} items are being published...`);

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
        const allPublishable = this.isAllPublishable();
        const allValid = this.areItemsAndDependantsValid();

        let subTitle = (count === 0) ?
                            'No items to publish' :
                            allPublishable ?
                                (allValid ?
                                    'Your changes are ready for publishing' :
                                    'Invalid item(s) prevent publishing'
                                ) :
                                'Create a new Publishing Issue with selected item(s)';

        this.setSubTitle(subTitle);
        this.toggleClass('invalid', !allValid && allPublishable);
    }

    protected updateButtonCount(actionString: string, count: number) {
        const canPublish = count > 0 && this.areItemsAndDependantsValid();

        this.updateButtonStatus(canPublish);

        if (canPublish) {
            this.getButtonRow().focusDefaultAction();
            this.updateTabbable();
        }

        const labelWithNumber = (count, label) => `${label}${count > 1 ? ` (${count})` : '' }`;

        this.publishButton.getAction().setLabel(labelWithNumber(count, 'Publish'));
        this.createIssueButton.getAction().setLabel(labelWithNumber(count, 'Create Issue... '));
    }

    protected updateButtonStatus(enabled: boolean) {
        if (this.isAllPublishable()) {
            this.toggleAction(enabled);
        } else {
            this.toggleAction(true);
        }
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
        return !this.containsInvalid;
    }

    protected hasSubDialog(): boolean {
        return true;
    }

    protected lockControls() {
        super.lockControls();
        this.publishButton.getAction().setEnabled(false);
        this.createIssueButton.getAction().setEnabled(false);
    }

    protected unlockControls() {
        super.unlockControls();
        this.publishButton.getAction().setEnabled(true);
        this.createIssueButton.getAction().setEnabled(true);
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

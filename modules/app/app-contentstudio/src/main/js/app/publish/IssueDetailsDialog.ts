import '../../api.ts';
import {PublishDialogDependantList} from './PublishDialogDependantList';
import {ContentPublishPromptEvent} from '../browse/ContentPublishPromptEvent';
import {PublishDialogItemList, PublicStatusSelectionItem} from './PublishDialogItemList';
import {Issue} from './Issue';
import {IssueDialogForm} from './IssueDialogForm';
import {ContentPublishDialogAction} from './ContentPublishDialog';
import {SchedulableDialog} from '../dialog/SchedulableDialog';
import {ProgressBarConfig} from '../dialog/ProgressBarDialog';
import {IssueListItem} from './IssueList';
import {Router} from '../Router';

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import PublishContentRequest = api.content.resource.PublishContentRequest;
import ResolvePublishDependenciesResult = api.content.resource.result.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ContentId = api.content.ContentId;
import ListBox = api.ui.selector.list.ListBox;
import LoadMask = api.ui.mask.LoadMask;
import BrowseItem = api.app.browse.BrowseItem;
import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;
import ResolvePublishDependenciesRequest = api.content.resource.ResolvePublishDependenciesRequest;
import ContentSummaryAndCompareStatusFetcher = api.content.resource.ContentSummaryAndCompareStatusFetcher;
import Checkbox = api.ui.Checkbox;
import InputAlignment = api.ui.InputAlignment;
import AEl = api.dom.AEl;
import DialogButton = api.ui.dialog.DialogButton;
import IssuePublishedNotificationRequest = api.issue.resource.IssuePublishedNotificationRequest;

export class IssueDetailsDialog extends SchedulableDialog {

    private form: IssueDialogForm;

    private issue: Issue;

    private editButton: AEl;

    private backButton: DialogButton;

    private closeOnPublishCheckbox: Checkbox;

    private itemsHeader: api.dom.H6El;

    private static INSTANCE: IssueDetailsDialog = new IssueDetailsDialog();

    constructor() {
        super(<ProgressBarConfig> {
                dialogName: 'Issue Details',
                dialogSubName: 'Resolving items...',
                dependantsName: '',
                isProcessingClass: 'is-publishing',
                processHandler: () => {
                    new ContentPublishPromptEvent([]).fire();
                },
            }
        );

        this.addClass('issue-details-dialog');

        this.setAutoUpdateTitle(false);

        this.initRouting();

        this.form = new IssueDialogForm();
        this.prependChildToContentPanel(this.form);

        this.createEditButton();
        this.createBackButton();

        this.initActions();

        this.itemsHeader = new api.dom.H6El().addClass('items-header').setHtml('Items:').insertBeforeEl(this.getItemList());

    }

    public static get(): IssueDetailsDialog {
        return IssueDetailsDialog.INSTANCE;
    }

    private initRouting() {
        this.onShown(() => {
            Router.setHash('issue/' + this.issue.getId());
        });

        this.onClosed(Router.back);
    }

    setIssue(issue: Issue): IssueDetailsDialog {
        this.issue = issue;

        if (this.issue.getPublishRequest().getItemsIds().length > 0) {
            this.lockControls();
        }

        this.form.setIssue(issue);
        this.form.setReadOnly(true);

        this.setTitle(issue.getTitle() + ' #' + issue.getIndex());
        this.initStatusInfo();

        if (this.getItemList().isRendered()) {
            this.initItemList();
        } else {
            this.getItemList().onItemsAdded(() => {
                this.initItemList();
            });
        }

        ContentSummaryAndCompareStatusFetcher.fetchByIds(
            this.issue.getPublishRequest().getItemsIds()).then((result) => {
            this.setListItems(result);
        });

        return this;
    }

    public toggleNested(value: boolean): IssueDetailsDialog {
        this.toggleClass('nested', value);
        return this;
    }

    private initStatusInfo() {
        this.setSubTitle(new IssueListItem(this.issue, 'issue').getStatusInfo(), false);
    }

    private initItemList() {
        this.getItemList().getItemViews()
            .filter(itemView => itemView.getIncludeChildrenToggler())
            .forEach(itemView => itemView.getIncludeChildrenToggler().toggle(this.isChildrenIncluded(itemView), true));

        this.getItemList().setReadOnly(true);
    }

    private isChildrenIncluded(itemView: PublicStatusSelectionItem) {
        return this.issue.getPublishRequest().getExcludeChildrenIds().map(contentId => contentId.toString()).indexOf(
                itemView.getBrowseItem().getId()) < 0;
    }

    protected initActions() {
        const publishAction = new ContentPublishDialogAction(this.doPublish.bind(this, false));
        this.actionButton = this.addAction(publishAction, true);

        super.initActions();

        if (!this.closeOnPublishCheckbox) {
            this.closeOnPublishCheckbox =
                Checkbox.create().setInputAlignment(InputAlignment.RIGHT).setLabelText('Close issue on publish').build();
            this.getButtonRow().addElement(this.closeOnPublishCheckbox);
        }
    }

    private createEditButton(): api.dom.AEl {
        this.appendChildToHeader(this.editButton = new api.dom.AEl('edit').setTitle('Edit Issue'));
        return this.editButton;
    }

    private createBackButton(): DialogButton {
        return this.backButton = this.addCancelButtonToBottom('Back');
    }

    private doPublish(scheduled: boolean) {

        const selectedIds = this.issue.getPublishRequest().getItemsIds();
        const excludedIds = this.issue.getPublishRequest().getExcludeIds();
        const excludedChildrenIds = this.issue.getPublishRequest().getExcludeChildrenIds();

        let publishRequest = new PublishContentRequest()
            .setIds(selectedIds)
            .setExcludedIds(excludedIds)
            .setExcludeChildrenIds(excludedChildrenIds);

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

    protected onFinished() {
        super.onFinished();

        this.notifyIssuePublished();
    }

    protected createItemList(): ListBox<ContentSummaryAndCompareStatus> {
        return new PublishDialogItemList();
    }

    protected createDependantList(): PublishDialogDependantList {
        return new PublishDialogDependantList();
    }

    protected getItemList(): PublishDialogItemList {
        return <PublishDialogItemList>super.getItemList();
    }

    protected getDependantList(): PublishDialogDependantList {
        return <PublishDialogDependantList>super.getDependantList();
    }

    open() {
        this.form.giveFocus();
        this.reloadPublishDependencies().done();
        super.open();
    }

    close() {
        this.getItemList().clearExcludeChildrenIds();
        super.close();
    }

    private reloadPublishDependencies(): wemQ.Promise<void> {

        const deferred = wemQ.defer<void>();

        this.loadMask.show();

        const isItemsEmpty = this.issue.getPublishRequest().getItemsIds().length == 0;

        this.itemsHeader.setVisible(!isItemsEmpty);

        //no need to make request
        if(isItemsEmpty) {
            this.dependantIds = [];
            this.setDependantItems([]);

            this.updateButtonCount('Publish', 0);
            this.toggleAction(false);

            deferred.resolve(null);
        }

        const resolveDependenciesRequest = ResolvePublishDependenciesRequest.create().setIds(
            this.issue.getPublishRequest().getItemsIds()).setExcludedIds(
            this.issue.getPublishRequest().getExcludeIds()).setExcludeChildrenIds(
            this.issue.getPublishRequest().getExcludeChildrenIds()).build();

        resolveDependenciesRequest.sendAndParse().then((result: ResolvePublishDependenciesResult) => {

            this.dependantIds = result.getDependants().slice();

            this.toggleAction(!result.isContainsInvalid());

            this.loadDescendants(0, 20).then((dependants: ContentSummaryAndCompareStatus[]) => {
                this.setDependantItems(dependants);
                this.loadMask.hide();

                const countToPublish = this.countTotal();
                this.updateButtonCount('Publish', countToPublish);

                this.toggleAction(countToPublish > 0);

                this.centerMyself();
                deferred.resolve(null);
            });
        });
        return deferred.promise;
    }

    protected toggleAction(enable: boolean) {
        super.toggleAction(enable);
        this.closeOnPublishCheckbox.setDisabled(!enable);
    }

    private filterDependantItems(dependants: ContentSummaryAndCompareStatus[]) {
        let itemsToRemove = this.getDependantList().getItems().filter(
            (oldDependantItem: ContentSummaryAndCompareStatus) => !dependants.some(
                (newDependantItem) => oldDependantItem.equals(newDependantItem)));
        this.getDependantList().removeItems(itemsToRemove);
    }

    setIncludeChildItems(include: boolean, silent?: boolean) {
        this.getItemList().getItemViews()
            .filter(itemView => itemView.getIncludeChildrenToggler())
            .forEach(itemView => itemView.getIncludeChildrenToggler().toggle(include, silent)
            );
        return this;
    }

    private areSomeItemsOffline(): boolean {
        let summaries: ContentSummaryAndCompareStatus[] = this.getItemList().getItems();
        return summaries.some((summary) => summary.getCompareStatus() === CompareStatus.NEW);
    }

    protected doScheduledAction() {
        this.doPublish(true);
        this.close();
    }

    protected isScheduleButtonAllowed(): boolean {
        return this.areSomeItemsOffline();
    }

    protected hasSubDialog(): boolean {
        return true;
    }

    private notifyIssuePublished() {
        new IssuePublishedNotificationRequest(this.issue.getId()).send();
    }
}

export class ShowIssueDetailsDialogAction extends api.ui.Action {
    constructor() {
        super();
        this.setIconClass('show-schedule-action');
    }
}

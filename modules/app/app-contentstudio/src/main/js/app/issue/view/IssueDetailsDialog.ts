import {IssueDialogForm} from './IssueDialogForm';
import {SchedulableDialog} from '../../dialog/SchedulableDialog';
import {Issue} from '../Issue';
import {UpdateIssueDialog} from './UpdateIssueDialog';
import {ProgressBarConfig} from '../../dialog/ProgressBarDialog';
import {ContentPublishPromptEvent} from '../../browse/ContentPublishPromptEvent';
import {Router} from '../../Router';
import {PublicStatusSelectionItem, PublishDialogItemList} from '../../publish/PublishDialogItemList';
import {ContentPublishDialogAction} from '../../publish/ContentPublishDialog';
import {PublishDialogDependantList} from '../../publish/PublishDialogDependantList';
import {UpdateIssueRequest} from '../resource/UpdateIssueRequest';
import {IssueStatus} from '../IssueStatus';
import AEl = api.dom.AEl;
import DialogButton = api.ui.dialog.DialogButton;
import Checkbox = api.ui.Checkbox;
import ContentSummaryAndCompareStatusFetcher = api.content.resource.ContentSummaryAndCompareStatusFetcher;
import InputAlignment = api.ui.InputAlignment;
import PublishContentRequest = api.content.resource.PublishContentRequest;
import TaskState = api.task.TaskState;
import ListBox = api.ui.selector.list.ListBox;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ResolvePublishDependenciesResult = api.content.resource.result.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ResolvePublishDependenciesRequest = api.content.resource.ResolvePublishDependenciesRequest;
import DateHelper = api.util.DateHelper;

export class IssueDetailsDialog extends SchedulableDialog {

    private form: IssueDialogForm;

    private issue: Issue;

    private editButton: AEl;

    private backButton: DialogButton;

    private closeOnPublishCheckbox: Checkbox;

    private itemsHeader: api.dom.H6El;

    private issueIdEl: api.dom.EmEl;

    private updateIssueDialog: UpdateIssueDialog;

    private issueClosedListeners: ((issue: Issue) => void)[] = [];

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

        this.issueIdEl = new api.dom.EmEl('issue-id');
        this.header.appendElement(this.issueIdEl);


        this.getItemList().onItemsAdded(() => {
            this.initItemList();
        });

        this.setReadOnly(true);
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

    setReadOnly(value: boolean) {
        this.form.setReadOnly(value);
        this.getItemList().setReadOnly(value);
        this.getDependantList().setReadOnly(value);
    }

    setIssue(issue: Issue): IssueDetailsDialog {
        this.issue = issue;

        this.form.setIssue(issue);

        this.setTitle(issue.getTitle());

        this.issueIdEl.setHtml('#' + issue.getIndex());

        this.initStatusInfo();

        this.reloadPublishDependencies().then(() => {
            ContentSummaryAndCompareStatusFetcher.fetchByIds(
                this.issue.getPublishRequest().getItemsIds()).then((result) => {
                this.setListItems(result);

                const countToPublish = this.countTotal();
                this.updateButtonCount('Publish', countToPublish);

                this.toggleAction(countToPublish > 0);


                if (this.issue.getPublishRequest().getItemsIds().length > 0) {
                    this.unlockControls();
                } else {
                    this.lockControls();
                }
                this.centerMyself();
            });
        });

        return this;
    }

    public toggleNested(value: boolean): IssueDetailsDialog {
        this.toggleClass('nested', value);
        return this;
    }

    private initStatusInfo() {
        this.setSubTitle(this.makeStatusInfo(), false);
    }

    private makeStatusInfo(): string {
        return 'Opened by ' + '\<span class="creator"\>' + this.issue.getCreator() + '\</span\> ' +
               DateHelper.getModifiedString(this.issue.getModifiedTime());
    }

    private initItemList() {
        this.getItemList().getItemViews()
            .filter(itemView => itemView.getIncludeChildrenToggler())
            .forEach(itemView => itemView.getIncludeChildrenToggler().toggle(this.isChildrenIncluded(itemView)));

        this.setReadOnly(true);

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

        this.editButton.onClicked(() => {
            this.showUpdateIssueDialog();
        });

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
            const issue = this.issue;
            const closeIssue = this.closeOnPublishCheckbox.isChecked();
            const issuePublishedHandler = (taskState: TaskState) => {
                if (taskState === TaskState.FINISHED && closeIssue) {
                    new UpdateIssueRequest(issue.getId())
                        .setStatus(IssueStatus.CLOSED)
                        .setIsPublish(true)
                        .sendAndParse()
                        .then((updatedIssue: Issue) => {
                            this.notifyIssueClosed(updatedIssue);
                            api.notify.showFeedback(`Issue "${updatedIssue.getTitle()}" is closed`);
                        }).catch(() => {
                        api.notify.showError(`Can not close issue "${issue.getTitle()}"`);
                    }).finally(() => {
                        this.unProgressComplete(issuePublishedHandler);
                    });
                }
            };
            this.onProgressComplete(issuePublishedHandler);
            this.pollTask(taskId);
        }).catch((reason) => {
            this.unlockControls();
            this.close();
            if (reason && reason.message) {
                api.notify.showError(reason.message);
            }
        });
    }

    private showUpdateIssueDialog() {
        if (!this.updateIssueDialog) {
            this.updateIssueDialog = UpdateIssueDialog.get();

            this.updateIssueDialog.onClosed(() => {
                this.removeClass('masked');
                this.getEl().focus();
            });

            this.updateIssueDialog.onSucceed((issue: Issue) => {
                this.setIssue(issue);
            });

            this.addClickIgnoredElement(this.updateIssueDialog);
        }

        this.updateIssueDialog.open();
        this.updateIssueDialog.unlockPublishItems();

        this.updateIssueDialog.setIssue(this.issue, this.getItemList().getItems());

        this.updateIssueDialog.setExcludeChildrenIds(this.getItemList().getExcludeChildrenIds());

        this.addClass('masked');
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
        if (isItemsEmpty) {
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

    onIssueClosed(listener: (issue: Issue) => void) {
        this.issueClosedListeners.push(listener);
    }

    unIssueClosed(listener: (issue: Issue) => void) {
        this.issueClosedListeners = this.issueClosedListeners.filter((curr: (issue: Issue) => void) => {
            return curr !== listener;
        });
    }

    private notifyIssueClosed(issue: Issue) {
        this.issueClosedListeners.forEach((listener) => {
            listener(issue);
        });
    }
}

export class ShowIssueDetailsDialogAction extends api.ui.Action {
    constructor() {
        super();
        this.setIconClass('show-schedule-action');
    }
}

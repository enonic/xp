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
import {IssueStatus, IssueStatusFormatter} from '../IssueStatus';
import {IssueStatusSelector} from './IssueStatusSelector';
import {IssueServerEventsHandler} from '../event/IssueServerEventsHandler';
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
import H6El = api.dom.H6El;
import PEl = api.dom.PEl;
import SpanEl = api.dom.SpanEl;
import DivEl = api.dom.DivEl;
import RequestError = api.rest.RequestError;
import MenuButton = api.ui.button.MenuButton;
import Action = api.ui.Action;
import DropdownButtonRow = api.ui.dialog.DropdownButtonRow;
import {IssueDetailsDialogButtonRow} from './IssueDetailsDialogDropdownButtonRow';


export class IssueDetailsDialog extends SchedulableDialog {

    private form: IssueDialogForm;

    private issue: Issue;

    private closeOnPublishCheckbox: Checkbox;

    private itemsHeader: api.dom.H6El;

    private issueIdEl: api.dom.EmEl;

    private static INSTANCE: IssueDetailsDialog = new IssueDetailsDialog();

    private constructor() {
        super(<ProgressBarConfig> {
                dialogName: 'Issue Details',
                dialogSubName: 'Resolving items...',
                dependantsName: '',
                isProcessingClass: 'is-publishing',
                buttonRow: new IssueDetailsDialogButtonRow(),
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
        this.handleUpdateIssueDialogEvents();
        this.handleIssueGlobalEvents();

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

    private handleIssueGlobalEvents() {
        const updateHandler: Function = api.util.AppHelper.debounce((issue: Issue) => {
            this.setIssue(issue);
        }, 3000, true);

        IssueServerEventsHandler.getInstance().onIssueUpdated((issues: Issue[]) => {
            if (this.isVisible()) {
                if (issues.some((issue) => issue.getId() === this.issue.getId())) {
                    updateHandler(issues[0]);
                }
            }
        });
    }

    private handleUpdateIssueDialogEvents() {
        this.addClickIgnoredElement(UpdateIssueDialog.get());

        UpdateIssueDialog.get().onClosed(() => {
            this.removeClass('masked');
            if (this.isVisible()) {
                this.getEl().focus();
            }
        });
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
            this.centerMyself();
        });

        return this;
    }

    public toggleNested(value: boolean): IssueDetailsDialog {
        this.toggleClass('nested', value);
        return this;
    }

    getButtonRow(): IssueDetailsDialogButtonRow {
        return <IssueDetailsDialogButtonRow>super.getButtonRow();
    }

    private initStatusInfo() {
        const title = this.makeStatusInfo();

        title.onIssueStatusChanged((event) => {

            const newStatus = IssueStatusFormatter.fromString(event.getNewValue());

            new UpdateIssueRequest(this.issue.getId())
                .setStatus(newStatus).sendAndParse().then(() => {
                api.notify.showFeedback(`The issue is ` + event.getNewValue().toLowerCase());

                this.toggleControlsAccordingToStatus(newStatus);

            }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
        });

        this.setSubTitleEl(title);
        this.toggleControlsAccordingToStatus(this.issue.getIssueStatus());
    }


    private makeStatusInfo(): DetailsDialogSubTitle {
        return DetailsDialogSubTitle.create().setCreator(this.issue.getCreator()).setModifiedTime(
            this.issue.getModifiedTime()).setIssueStatus(this.issue.getIssueStatus()).build();

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
        super.initActions();

        const publishAction = new ContentPublishDialogAction(this.doPublish.bind(this, false));
        const actionMenu: MenuButton = this.getButtonRow().makeActionMenu(publishAction, [this.showScheduleAction]);


        this.actionButton = actionMenu.getActionButton();

        if (!this.closeOnPublishCheckbox) {
            this.closeOnPublishCheckbox =
                Checkbox.create().setInputAlignment(InputAlignment.RIGHT).setLabelText('Close issue on publish').build();
            this.getButtonRow().addElement(this.closeOnPublishCheckbox);
        }
    }

    private createEditButton() {
        const editButton: api.dom.AEl = new api.dom.AEl('edit').setTitle('Edit Issue');
        this.appendChildToHeader(editButton);

        editButton.onClicked(() => {
            this.showUpdateIssueDialog();
        });
    }

    private createBackButton() {
        this.addCancelButtonToBottom('Back');
    }

    private doPublish(scheduled: boolean) {

        const selectedIds = this.getItemList().getItems().map(item => item.getContentId());
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
        UpdateIssueDialog.get().open();
        UpdateIssueDialog.get().unlockPublishItems();
        UpdateIssueDialog.get().setIssue(this.issue, this.getItemList().getItems());
        UpdateIssueDialog.get().setExcludeChildrenIds(this.getItemList().getExcludeChildrenIds());

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

        ContentSummaryAndCompareStatusFetcher.fetchByIds(
            this.issue.getPublishRequest().getItemsIds()).then((result) => {

            if (result.length != this.issue.getPublishRequest().getItemsIds().length) {
                api.notify.showWarning('One or more items from the issue cannot be found');
            }

            this.setListItems(result);

            const resolveDependenciesRequest = ResolvePublishDependenciesRequest.create().setIds(
                result.map(content => content.getContentId())).setExcludedIds(
                this.issue.getPublishRequest().getExcludeIds()).setExcludeChildrenIds(
                this.issue.getPublishRequest().getExcludeChildrenIds()).build();

            resolveDependenciesRequest.sendAndParse().then((result: ResolvePublishDependenciesResult) => {
                this.dependantIds = result.getDependants().slice();

                const countToPublish = this.countTotal();
                this.updateButtonCount('Publish', countToPublish);

                this.toggleAction(countToPublish > 0 && !result.isContainsInvalid());

                this.loadDescendants(0, 20).then((dependants: ContentSummaryAndCompareStatus[]) => {
                    this.setDependantItems(dependants);

                    this.loadMask.hide();
                    this.centerMyself();

                    deferred.resolve(null);
                });
            }).catch((reason: RequestError) => {
                this.loadMask.hide();
                deferred.reject(reason);
            });
        });

        return deferred.promise;
    }

    protected toggleAction(enable: boolean) {
        super.toggleAction(enable);
        this.closeOnPublishCheckbox.setDisabled(!enable);
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

    private toggleControlsAccordingToStatus(status: IssueStatus) {
        this.toggleClass('closed', (status == IssueStatus.CLOSED));
    }

    protected isScheduleButtonAllowed(): boolean {
        return this.areSomeItemsOffline();
    }

    protected hasSubDialog(): boolean {
        return true;
    }
}

class DetailsDialogSubTitle extends DivEl {

    private creator: string;

    private modifiedTime: Date;

    private status: IssueStatus;

    private issueStatusChangedListeners: {(event: api.ValueChangedEvent): void}[] = [];

    constructor(builder: DetailsDialogSubTitleBuilder) {
        super('issue-details-sub-title');
        this.creator = builder.creator;
        this.modifiedTime = builder.modifiedTime;
        this.status = builder.status;
    }

    doRender(): wemQ.Promise<boolean> {

        return super.doRender().then(() => {
            const issueStatusSelector = new IssueStatusSelector().setValue(this.status);
            issueStatusSelector.onValueChanged((event) => {
                this.notifyIssueStatusChanged(event);
            });

            this.appendChild(issueStatusSelector);

            this.appendChild(new SpanEl().setHtml('Opened by '));
            this.appendChild(new SpanEl('creator').setHtml(this.creator + ' '));
            this.appendChild(new SpanEl().setHtml(DateHelper.getModifiedString(this.modifiedTime)));

            return wemQ(true);
        });
    }

    onIssueStatusChanged(listener: (event: api.ValueChangedEvent)=>void) {
        this.issueStatusChangedListeners.push(listener);
    }

    unIssueStatusChanged(listener: (event: api.ValueChangedEvent)=>void) {
        this.issueStatusChangedListeners = this.issueStatusChangedListeners.filter((curr) => {
            return curr !== listener;
        });
    }

    private notifyIssueStatusChanged(event: api.ValueChangedEvent) {
        this.issueStatusChangedListeners.forEach((listener) => {
            listener(event);
        });
    }

    static create(): DetailsDialogSubTitleBuilder {
        return new DetailsDialogSubTitleBuilder();
    }

}
class DetailsDialogSubTitleBuilder {

    creator: string;

    modifiedTime: Date;

    status: IssueStatus;

    setCreator(value: string): DetailsDialogSubTitleBuilder {
        this.creator = value;
        return this;
    }

    setModifiedTime(value: Date): DetailsDialogSubTitleBuilder {
        this.modifiedTime = value;
        return this;
    }

    setIssueStatus(value: IssueStatus): DetailsDialogSubTitleBuilder {
        this.status = value;
        return this;
    }

    build() {
        return new DetailsDialogSubTitle(this);
    }
}

export class ShowIssueDetailsDialogAction extends api.ui.Action {
    constructor() {
        super();
        this.setIconClass('show-schedule-action');
    }
}

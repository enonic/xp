import '../../api.ts';
import {IssueDialogForm} from './IssueDialogForm';
import {DependantItemsDialog} from '../dialog/DependantItemsDialog';
import {PublishProcessor} from './PublishProcessor';
import {PublishDialogItemList} from './PublishDialogItemList';
import {PublishDialogDependantList} from './PublishDialogDependantList';
import PublishRequestItem = api.issue.PublishRequestItem;
import CreateIssueRequest = api.issue.resource.CreateIssueRequest;
import PublishRequest = api.issue.PublishRequest;
import ContentSummaryAndCompareStatusFetcher = api.content.resource.ContentSummaryAndCompareStatusFetcher;
import ListBox = api.ui.selector.list.ListBox;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ContentId = api.content.ContentId;
import ObjectHelper = api.ObjectHelper;
import ContentSummary = api.content.ContentSummary;

export class CreateIssueDialog extends DependantItemsDialog {

    private form: IssueDialogForm;

    private publishProcessor: PublishProcessor;

    private onSucceedListeners: {(): void;}[] = [];

    private static INSTANCE: CreateIssueDialog;

    private constructor() {
        super('Create Issue');

        this.publishProcessor = new PublishProcessor(this.getItemList(), this.getDependantList());

        this.getEl().addClass('create-issue-dialog');

        this.initForm();

        this.initActions();

        this.initFormView();

        this.addCancelButtonToBottom('Back');

        this.publishProcessor.onLoadingStarted(() => {
            this.updateButtonCount('Create Issue', 0);
            this.loadMask.show();
        });

        this.publishProcessor.onLoadingFinished(() => {
            this.updateButtonCount('Create Issue', this.countTotal());
            this.loadMask.hide();
        })

        this.onRendered(() => {
            this.publishProcessor.reloadPublishDependencies(true).then(() => {
                this.form.setContentItems(this.publishProcessor.getContentToPublishIds(), true);
                this.form.giveFocus();
                this.loadMask.hide();
            });
        });

        this.getItemList().setCanBeEmpty(true);

        this.getItemList().onItemsRemoved((items) => {
            this.form.deselectContentItems(items.map(item => item.getContentSummary()), true);
            this.centerMyself();
        });

        this.getItemList().onItemsAdded((items) => {
            this.form.selectContentItems(items.map(item => item.getContentSummary()), true);
        });
    }

    static get(): CreateIssueDialog {
        if (!CreateIssueDialog.INSTANCE) {
            CreateIssueDialog.INSTANCE = new CreateIssueDialog();
        }
        return CreateIssueDialog.INSTANCE;
    }

    public setItems(items: ContentSummaryAndCompareStatus[]) {

        this.setListItems(items);
        (<CreateIssueAction>this.actionButton.getAction()).updateLabel(this.countTotal());
    }

    public setExcludeChildrenIds(ids: ContentId[]) {
        this.getItemList().setExcludeChildrenIds(ids);
    }

    public setExcludedIds(ids: ContentId[]) {
        this.publishProcessor.setExcludedIds(ids);
    }

    public getExcludedIds(): ContentId[] {
        return this.publishProcessor.getExcludedIds();
    }

    public countTotal(): number {
        return this.publishProcessor.countTotal();
    }

    open() {
        super.open();
        this.form.giveFocus();
    }

    show() {
        this.displayValidationErrors(false);

        api.dom.Body.get().appendChild(this);
        super.show();
    }

    close() {
        super.close();
        this.remove();
    }

    private initForm() {
        this.form = new IssueDialogForm();

        this.form.onContentItemsAdded((items: ContentSummary[]) => {
            ContentSummaryAndCompareStatusFetcher.fetchByIds(
                items.map(summary => summary.getContentId())).then((result) => {

                this.setListItems(result.concat(this.getItemList().getItems()), true);

                this.publishProcessor.reloadPublishDependencies(true).then(() => this.centerMyself());
            });
        });

        this.form.onContentItemsRemoved((items) => {

            const filteredItems = this.getItemList().getItems().filter((oldItem: ContentSummaryAndCompareStatus) => {
                return !ObjectHelper.contains(items, oldItem.getContentSummary());
            });

            this.setListItems(filteredItems, true);

            this.publishProcessor.reloadPublishDependencies(true);

        });
    }

    private initFormView() {
        this.prependChildToContentPanel(this.form);
        this.centerMyself();
    }

    protected displayValidationErrors(value: boolean) {
        if (this.form) {
            this.form.displayValidationErrors(value);
        }
    }

    private doCreateIssue() {

        const valid = this.form.validate(true).isValid();

        this.displayValidationErrors(!valid);

        if (valid) {
            const createIssueRequest = new CreateIssueRequest()
                .setApprovers(this.form.getApprovers())
                .setPublishRequest(
                    PublishRequest.create()
                        .addExcludeIds(this.getExcludedIds())
                        .addPublishRequestItems(this.createPublishRequestItems())
                        .build()
                ).setDescription(this.form.getDescription()).setTitle(this.form.getTitle());

            createIssueRequest.sendAndParse().then(() => {
                this.close();
                this.notifySucceed();
                api.notify.showSuccess('New issue created successfully');
            }).catch((reason) => {
                if (reason && reason.message) {
                    api.notify.showError(reason.message);
                }
            });
        }
    }

    public reset() {
        this.form.reset();
        this.publishProcessor.reset();

        this.form.giveFocus();
    }

    private createPublishRequestItems(): PublishRequestItem[] {
        return this.getItemList().getItems().map(item => {
            return item.getContentId();
        }).map(contentId => {
            return PublishRequestItem.create()
                .setId(contentId)
                .setIncludeChildren(this.getItemList().getExcludeChildrenIds().indexOf(contentId) < 0)
                .build();
        });
    }

    public lockPublishItems() {
        this.getItemList().setReadOnly(true);
        this.getDependantList().setReadOnly(true);
        this.form.toggleContentItemsSelector(false);
    }

    public unlockPublishItems() {
        this.getItemList().setReadOnly(false);
        this.getDependantList().setReadOnly(false);
        this.form.toggleContentItemsSelector(true);
    }

    protected getDependantIds(): ContentId[] {
        return this.publishProcessor.getDependantIds();
    }

    protected createItemList(): ListBox<ContentSummaryAndCompareStatus> {
        return new PublishDialogItemList();
    }

    protected getItemList(): PublishDialogItemList {
        return <PublishDialogItemList>super.getItemList();
    }

    protected createDependantList(): PublishDialogDependantList {
        let dependants = new PublishDialogDependantList();

        return dependants;
    }

    protected getDependantList(): PublishDialogDependantList {
        return <PublishDialogDependantList>super.getDependantList();
    }


    private initActions() {
        const createAction = new CreateIssueAction(this.countTotal());
        createAction.onExecuted(this.doCreateIssue.bind(this));
        this.actionButton = this.addAction(createAction, true);

    }

    protected hasSubDialog(): boolean {
        return false;
    }

    onSucceed(onSucceedListener: () => void) {
        this.onSucceedListeners.push(onSucceedListener);
    }

    unSucceed(listener: {(): void;}) {
        this.onSucceedListeners = this.onSucceedListeners.filter(function (curr: {(): void;}) {
            return curr !== listener;
        });
    }

    private notifySucceed() {
        this.onSucceedListeners.forEach((listener) => {
            listener();
        });
    }
}

export class CreateIssueAction extends api.ui.Action {

    constructor(itemCount: number) {
        super();
        this.updateLabel(itemCount);
        this.setIconClass('create-issue-action');
    }

    public updateLabel(count: number) {
        let label = 'Create Issue ';
        if (count) {
            label += '(' + count + ')';
        }
        this.setLabel(label);
    }
}

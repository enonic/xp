import {DependantItemsDialog} from '../../dialog/DependantItemsDialog';
import {IssueDialogForm} from './IssueDialogForm';
import {PublishProcessor} from '../../publish/PublishProcessor';
import {PublishRequestItem} from '../PublishRequestItem';
import {PublishDialogItemList} from '../../publish/PublishDialogItemList';
import {PublishDialogDependantList} from '../../publish/PublishDialogDependantList';
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ContentSummary = api.content.ContentSummary;
import ContentSummaryAndCompareStatusFetcher = api.content.resource.ContentSummaryAndCompareStatusFetcher;
import ListBox = api.ui.selector.list.ListBox;
import ModalDialog = api.ui.dialog.ModalDialog;
import ArrayHelper = api.util.ArrayHelper;
import ContentTreeSelectorItem = api.content.resource.ContentTreeSelectorItem;

export abstract class IssueDialog extends DependantItemsDialog {

    protected form: IssueDialogForm;

    protected publishProcessor: PublishProcessor;

    private resetOnClose: boolean = false;

    private opener: ModalDialog;

    private debouncedAddItems: Function;

    private newItems: ContentSummary[] = [];

    protected constructor(title: string) {
        super(title);

        this.publishProcessor = new PublishProcessor(this.getItemList(), this.getDependantList());

        this.getEl().addClass('issue-dialog');

        this.initForm();

        this.initActions();

        this.initFormView();

        this.onRendered(() => {
            this.publishProcessor.reloadPublishDependencies(true).then(() => {
                this.form.setContentItems(this.publishProcessor.getContentToPublishIds(), true);
                this.form.giveFocus();
                this.loadMask.hide();
            });
        });

        this.getItemList().setCanBeEmpty(true);

        this.publishProcessor.onLoadingStarted(()=> {
            this.loadMask.show();
        });

        this.publishProcessor.onLoadingFinished(() => {
            this.loadMask.hide();
        });

        this.getItemList().onItemsRemoved((items) => {
            this.form.deselectContentItems(items.map(item => item.getContentSummary()), true);
        });

        this.getItemList().onItemsAdded((items) => {
            this.form.selectContentItems(items.map(item => item.getContentSummary()), true);
        });

        this.getDependantList().onItemsAdded(() => {
            setTimeout(() => this.centerMyself(), 100);
        });

        this.closeIcon.onClicked(() => this.opener ? this.opener.close() : true);

        this.debouncedAddItems = api.util.AppHelper.debounce(() => {
            ContentSummaryAndCompareStatusFetcher.fetchByIds(
                this.newItems.map(summary => summary.getContentId())).then((result) => {

                this.setListItems(this.getItemList().getItems().concat(result));

                this.publishProcessor.reloadPublishDependencies(true);

                this.newItems = [];
            });
        }, 100);
    }

    static get(): IssueDialog {
        throw new Error('must be implemented in inheritors');
    }

    protected initActions() {
        throw new Error('must be implemented in inheritors');
    }

    public setItems(items: ContentSummaryAndCompareStatus[]) {
        this.setListItems(items);
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
        return this.getItemList().getItemCount();
    }

    open(opener?: ModalDialog) {
        super.open();
        this.form.giveFocus();

        this.opener = opener;
    }

    show() {
        this.displayValidationErrors(false);

        super.show();
    }

    close() {
        if (this.resetOnClose) {
            this.resetOnClose = false;
            this.reset();
        }
        if (this.opener) {
            this.opener.removeClass('masked');
        }

        super.close();
    }

    private addNewItemsHandler(items: ContentSummary[]) {
        this.newItems = this.newItems.concat(items);
        this.debouncedAddItems();
    }

    private initForm() {
        this.form = new IssueDialogForm();

        this.form.onContentItemsAdded((items: ContentTreeSelectorItem[]) => {
            const contents: ContentSummary[] = items.map(item => item.getContent());

            this.addNewItemsHandler(contents);
        });

        this.form.onContentItemsRemoved((items: ContentTreeSelectorItem[]) => {

            const contents: ContentSummary[] = items.map(item => item.getContent());

            const filteredItems = this.getItemList().getItems().filter((oldItem: ContentSummaryAndCompareStatus) => {
                return !ArrayHelper.contains(contents, oldItem.getContentSummary());
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

    public reset() {
        this.form.reset();
        this.publishProcessor.reset();

        this.form.giveFocus();
    }

    public forceResetOnClose(value: boolean): IssueDialog {
        this.resetOnClose = value;
        this.getEl().toggleClass('issue-dialog', value);

        return this;
    }

    protected createPublishRequestItems(): PublishRequestItem[] {
        return this.getItemList().getItems().map(item => {
            return item.getContentId();
        }).map(contentId => {
            return PublishRequestItem.create()
                .setId(contentId)
                .setIncludeChildren(!ArrayHelper.contains(this.getItemList().getExcludeChildrenIds(), contentId))
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

    protected hasSubDialog(): boolean {
        return false;
    }

}

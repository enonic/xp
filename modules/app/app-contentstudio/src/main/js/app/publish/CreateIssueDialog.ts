import '../../api.ts';
import {IssueDialogForm} from './IssueDialogForm';
import PublishRequestItem = api.issue.PublishRequestItem;
import CreateIssueRequest = api.issue.resource.CreateIssueRequest;
import PublishRequest = api.issue.PublishRequest;

export class CreateIssueDialog extends api.ui.dialog.ModalDialog {

    private form: IssueDialogForm;

    private items: PublishRequestItem[];

    private excludeIds: ContentId[];

    private fullContentCount: number = 0;

    private confirmButton: api.ui.dialog.DialogButton;

    private onSucceedListeners: {(): void;}[] = [];

    private static INSTANCE: CreateIssueDialog;

    private constructor() {
        super(<api.ui.dialog.ModalDialogConfig>{title: 'Create Issue'});

        this.getEl().addClass('create-issue-dialog');

        this.initForm();

        this.initActions();

        this.initFormView();

        this.addCancelButtonToBottom('Back');

    }

    static get(): CreateIssueDialog {
        if (!CreateIssueDialog.INSTANCE) {
            CreateIssueDialog.INSTANCE = new CreateIssueDialog();
        }
        return CreateIssueDialog.INSTANCE;
    }

    public setFullContentCount(value: number) {
        this.fullContentCount = value;
    }

    public setExcludeIds(excludeIds: ContentId[]) {
        this.excludeIds = excludeIds;
    }

    public setItems(contentIds: ContentId[] = [], excludeChildrenIds: ContentId[] = []) {
        this.items = contentIds.map(contentId => {
            return PublishRequestItem.create()
                .setId(contentId)
                .setIncludeChildren(excludeChildrenIds.indexOf(contentId) < 0)
                .build();
        });

        (<CreateIssueAction>this.confirmButton.getAction()).updateLabel(this.fullContentCount);
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
    }

    private initFormView() {
        this.appendChildToContentPanel(this.form);
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
                    PublishRequest.create().addPublishRequestItems(this.items).addExcludeIds(this.excludeIds).build()
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

        this.items = [];
        this.excludeIds = [];
        this.fullContentCount = 0;
    }

    private initActions() {
        const createAction = new CreateIssueAction(this.fullContentCount);
        createAction.onExecuted(this.doCreateIssue.bind(this));
        this.confirmButton = this.addAction(createAction, true);

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

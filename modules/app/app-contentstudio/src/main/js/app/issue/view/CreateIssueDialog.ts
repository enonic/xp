import {IssueDialog} from './IssueDialog';
import {CreateIssueRequest} from '../resource/CreateIssueRequest';
import {PublishRequest} from '../PublishRequest';
import {IssueDetailsDialog} from './IssueDetailsDialog';
import LabelEl = api.dom.LabelEl;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import DialogButton = api.ui.dialog.DialogButton;
import AEl = api.dom.AEl;
import i18n = api.util.i18n;
import PrincipalKey = api.security.PrincipalKey;

export class CreateIssueDialog extends IssueDialog {

    private static INSTANCE: CreateIssueDialog;

    private itemsLabel: LabelEl;

    private cancelButton: DialogButton;

    private backButton: AEl;

    protected constructor() {
        super(i18n('dialog.newIssue'));

        this.getEl().addClass('create-issue-dialog');

        this.initElements();
        this.initElementsListeners();
    }

    static get(): CreateIssueDialog {
        if (!CreateIssueDialog.INSTANCE) {
            CreateIssueDialog.INSTANCE = new CreateIssueDialog();
        }
        return CreateIssueDialog.INSTANCE;
    }

    private initElements() {
        this.cancelButton = this.addCancelButtonToBottom();
        this.itemsLabel = new LabelEl(i18n('field.items'), this.getItemList());
        this.backButton = this.createBackButton();
    }

    private initElementsListeners() {
        let onItemsChanged = (items) => {
            (<CreateIssueAction>this.actionButton.getAction()).updateLabel(items.length);
        };

        this.getItemList().onItemsAdded(onItemsChanged);
        this.getItemList().onItemsRemoved(onItemsChanged);
    }

    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered: boolean) => {
            this.itemsLabel.insertBeforeEl(this.getItemList());
            this.prependChildToHeader(this.backButton);
            return rendered;
        });
    }

    public setItems(items: ContentSummaryAndCompareStatus[]): CreateIssueDialog {
        super.setItems(items);
        (<CreateIssueAction>this.actionButton.getAction()).updateLabel(this.countTotal());

        return this;
    }

    private doCreateIssue() {

        const valid = this.form.validate(true).isValid();

        this.displayValidationErrors(!valid);

        if (valid) {
            const approvers: PrincipalKey[] = this.form.getApprovers();
            const createIssueRequest = new CreateIssueRequest()
                .setApprovers(approvers)
                .setPublishRequest(
                    PublishRequest.create()
                        .addExcludeIds(this.getExcludedIds())
                        .addPublishRequestItems(this.createPublishRequestItems())
                        .build()
                ).setDescription(this.form.getDescription()).setTitle(this.form.getTitle());

            createIssueRequest.sendAndParse().then((issue) => {
                api.notify.showSuccess(i18n('notify.issue.created'));
                if ( approvers.length > issue.getApprovers().length ) {
                    api.notify.showWarning(i18n('notify.issue.asiignees.norights'));
                }
                this.close();
                this.reset();
                IssueDetailsDialog.get().setIssue(issue).open();
            }).catch((reason) => {
                if (reason && reason.message) {
                    api.notify.showError(reason.message);
                }
            });
        }
    }

    public lockPublishItems() {
        this.itemsLabel.show();

        super.lockPublishItems();
    }

    public unlockPublishItems() {
        this.itemsLabel.hide();
        super.unlockPublishItems();
    }

    protected initActions() {
        const createAction = new CreateIssueAction(this.countTotal());
        createAction.onExecuted(this.doCreateIssue.bind(this));
        this.actionButton = this.addAction(createAction, true);
    }

    public enableBackButton() {
        this.backButton.show();
        this.cancelButton.hide();
    }

    private disableBackButton() {
        this.backButton.hide();
        this.cancelButton.show();
    }

    private createBackButton(): AEl {
        const backButton: AEl = new AEl('back-button').setTitle(i18n('action.back'));

        backButton.hide();

        backButton.onClicked(() => {
            this.close();
        });

        return backButton;
    }

    close() {
        super.close();
        this.disableBackButton();
    }
}

export class CreateIssueAction extends api.ui.Action {

    constructor(itemCount: number) {
        super();
        this.updateLabel(itemCount);
        this.setIconClass('create-issue-action');
    }

    public updateLabel(count: number) {
        let label = i18n('action.createIssue');
        if (count > 1) {
            label += ' (' + count + ')';
        }
        this.setLabel(label);
    }
}

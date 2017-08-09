import {IssueDialog} from './IssueDialog';
import {Issue} from '../Issue';
import {UpdateIssueRequest} from '../resource/UpdateIssueRequest';
import {PublishRequest} from '../PublishRequest';
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import i18n = api.util.i18n;
import PrincipalKey = api.security.PrincipalKey;

export class UpdateIssueDialog extends IssueDialog {

    private static INSTANCE: UpdateIssueDialog;

    private persistedIssue: Issue;

    protected constructor() {
        super(i18n('action.editIssue'));

        this.getEl().addClass('update-issue-dialog');

        this.addCancelButtonToBottom(i18n('action.cancel'));
    }

    static get(): UpdateIssueDialog {
        if (!UpdateIssueDialog.INSTANCE) {
            UpdateIssueDialog.INSTANCE = new UpdateIssueDialog();
        }
        return UpdateIssueDialog.INSTANCE;
    }

    public setIssue(issue: Issue, items: ContentSummaryAndCompareStatus[]) {
        this.persistedIssue = issue;
        this.form.setIssue(issue);

        this.setExcludedIds(this.persistedIssue.getPublishRequest().getExcludeIds());

        this.setItems(items);
    }

    private doUpdateIssue() {

        const valid = this.form.validate(true).isValid();

        this.displayValidationErrors(!valid);

        if (valid) {
            const approvers: PrincipalKey[] = this.form.getApprovers();
            const updateIssueRequest = new UpdateIssueRequest(this.persistedIssue.getId())
                .setTitle(this.form.getTitle())
                .setDescription(this.form.getDescription())
                .setApprovers(approvers)
                .setStatus(this.persistedIssue.getIssueStatus())
                .setPublishRequest(
                    PublishRequest.create()
                        .addExcludeIds(this.getExcludedIds())
                        .addPublishRequestItems(this.createPublishRequestItems())
                        .build()
                );

            updateIssueRequest.sendAndParse().then((issue) => {
                api.notify.showSuccess(i18n('notify.issue.updated'));
                if ( approvers.length > issue.getApprovers().length ) {
                    api.notify.showWarning(i18n('notify.issue.asiignees.norights'));
                }
                this.close();
            }).catch((reason) => {
                if (reason && reason.message) {
                    api.notify.showError(reason.message);
                }
            });
        }
    }

    protected initActions() {
        const updateAction = new UpdateIssueAction();
        updateAction.onExecuted(this.doUpdateIssue.bind(this));
        this.actionButton = this.addAction(updateAction, true);
    }

    close() {
        this.reset();
        super.close();
    }
}

export class UpdateIssueAction extends api.ui.Action {
    constructor() {
        super();
        this.setIconClass('update-issue-action');
        this.setLabel(i18n('action.save'));
    }
}

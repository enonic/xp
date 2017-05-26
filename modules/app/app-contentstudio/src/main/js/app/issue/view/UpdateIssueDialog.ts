import {IssueDialog} from './IssueDialog';
import {Issue} from '../Issue';
import {UpdateIssueRequest} from '../resource/UpdateIssueRequest';
import {PublishRequest} from '../PublishRequest';
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class UpdateIssueDialog extends IssueDialog {

    private static INSTANCE: UpdateIssueDialog;

    private persistedIssue: Issue;

    protected constructor() {
        super('Update Issue');

        this.getEl().addClass('update-issue-dialog');

        this.addCancelButtonToBottom('Cancel');

        this.publishProcessor.onLoadingStarted(()=> {
            this.loadMask.show();
        });

        this.publishProcessor.onLoadingFinished(() => {
            this.loadMask.hide();
        });
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

        this.setItems(items);
    }

    private doUpdateIssue() {

        const valid = this.form.validate(true).isValid();

        this.displayValidationErrors(!valid);

        if (valid) {
            const updateIssueRequest = new UpdateIssueRequest(this.persistedIssue.getId())
                .setTitle(this.form.getTitle())
                .setDescription(this.form.getDescription())
                .setApprovers(this.form.getApprovers())
                .setStatus(this.persistedIssue.getIssueStatus())
                .setPublishRequest(
                    PublishRequest.create()
                        .addExcludeIds(this.getExcludedIds())
                        .addPublishRequestItems(this.createPublishRequestItems())
                        .build()
                );

            updateIssueRequest.sendAndParse().then((issue) => {
                api.notify.showSuccess('Issue has been updated');
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
}

export class UpdateIssueAction extends api.ui.Action {
    constructor() {
        super();
        this.setIconClass('update-issue-action');
        this.setLabel('Save');
    }
}


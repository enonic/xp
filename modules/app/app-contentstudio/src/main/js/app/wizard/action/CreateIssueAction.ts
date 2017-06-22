import '../../../api.ts';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {CreateIssuePromptEvent} from '../../browse/CreateIssuePromptEvent';
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import i18n = api.util.i18n;

export class CreateIssueAction extends api.ui.Action {

    private wizard: ContentWizardPanel;

    constructor(wizard: ContentWizardPanel) {
        super(i18n('action.createIssueMore'));

        this.wizard = wizard;

        this.onExecuted(() => {
            if (this.wizard.hasUnsavedChanges()) {
                this.setEnabled(false);
                this.wizard.saveChanges().then((content) => {
                    if (content) {
                        this.fireContentCreateIssuePromptEvent();
                    }
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => this.setEnabled(true)).done();
            } else {
                this.fireContentCreateIssuePromptEvent();
            }
        });
    }

    private fireContentCreateIssuePromptEvent(): void {
        const content = ContentSummaryAndCompareStatus.fromContentSummary(this.wizard.getPersistedItem());
        content.setCompareStatus(this.wizard.getCompareStatus()).
            setPublishStatus(this.wizard.getPublishStatus());
        new CreateIssuePromptEvent([content]).fire();
    }
}

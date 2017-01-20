import '../../../api.ts';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {ContentUnpublishPromptEvent} from '../../browse/ContentUnpublishPromptEvent';
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class UnpublishAction extends api.ui.Action {

    private wizard: ContentWizardPanel;

    constructor(wizard: ContentWizardPanel) {
        super('Unpublish');

        this.wizard = wizard;

        this.onExecuted(() => {
            if (this.wizard.hasUnsavedChanges()) {
                this.setEnabled(false);
                this.wizard.saveChanges().then((content) => {
                    if (content) {
                        this.fireContentUnpublishPromptEvent();
                    }
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => this.setEnabled(true)).done();
            } else {
                this.fireContentUnpublishPromptEvent();
            }
        });
    }

    private fireContentUnpublishPromptEvent(): void {
        let contentSummary = ContentSummaryAndCompareStatus.fromContentSummary(this.wizard.getPersistedItem());
        contentSummary.setCompareStatus(this.wizard.getContentCompareStatus()).
            setPublishStatus(this.wizard.getContentPublishStatus());
        new ContentUnpublishPromptEvent([contentSummary]).fire();
    }
}

import '../../../api.ts';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {ContentUnpublishPromptEvent} from '../../browse/ContentUnpublishPromptEvent';
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import i18n = api.util.i18n;

export class UnpublishAction extends api.ui.Action {

    private wizard: ContentWizardPanel;

    constructor(wizard: ContentWizardPanel) {
        super(i18n('action.unpublish'));

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
        let content = ContentSummaryAndCompareStatus.fromContentSummary(this.wizard.getPersistedItem());
        content.setCompareStatus(this.wizard.getCompareStatus()).
            setPublishStatus(this.wizard.getPublishStatus());
        new ContentUnpublishPromptEvent([content]).fire();
    }
}

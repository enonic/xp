import '../../../api.ts';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {ContentDeletePromptEvent} from '../../browse/ContentDeletePromptEvent';

import ContentId = api.content.ContentId;
import ContentPath = api.content.ContentPath;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class DeleteContentAction extends api.ui.Action {

    constructor(wizardPanel: ContentWizardPanel) {
        super('Delete...', 'mod+del', true);
        this.onExecuted(() => {
            new ContentDeletePromptEvent([new ContentSummaryAndCompareStatus().
                setContentSummary(wizardPanel.getPersistedItem()).
                setCompareStatus(wizardPanel.getContentCompareStatus()).
                setPublishStatus(wizardPanel.getContentPublishStatus())
            ]).fire();
        });
    }
}

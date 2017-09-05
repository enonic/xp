import '../../../api.ts';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {ContentDeletePromptEvent} from '../../browse/ContentDeletePromptEvent';

import ContentId = api.content.ContentId;
import ContentPath = api.content.ContentPath;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import i18n = api.util.i18n;

export class DeleteContentAction extends api.ui.Action {

    constructor(wizardPanel: ContentWizardPanel) {
        super(i18n('action.deleteMore'), 'mod+del', true);
        this.onExecuted(() => {
            new ContentDeletePromptEvent([new ContentSummaryAndCompareStatus().
                setContentSummary(wizardPanel.getPersistedItem()).
                setCompareStatus(wizardPanel.getCompareStatus()).
                setPublishStatus(wizardPanel.getPublishStatus())
            ]).fire();
        });
    }
}

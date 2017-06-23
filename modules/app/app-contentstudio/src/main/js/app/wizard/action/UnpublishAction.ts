import '../../../api.ts';
import {BasePublishAction} from './BasePublishAction';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {ContentUnpublishPromptEvent} from '../../browse/ContentUnpublishPromptEvent';

import i18n = api.util.i18n;

export class UnpublishAction extends BasePublishAction {
    constructor(wizard: ContentWizardPanel) {
        super({wizard, label: i18n('action.unpublish'), omitCanPublishCheck: true});
    }

    protected createPromptEvent(summary: api.content.ContentSummaryAndCompareStatus[]): void {
        new ContentUnpublishPromptEvent(summary).fire();
    }
}

import '../../../api.ts';
import {BasePublishAction} from './BasePublishAction';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {ContentPublishPromptEvent} from '../../browse/ContentPublishPromptEvent';

import i18n = api.util.i18n;

export class PublishTreeAction extends BasePublishAction {
    constructor(wizard: ContentWizardPanel) {
        super({wizard, label: i18n('action.publishTreeMore'), errorMessage: i18n('notify.publish.invalidError')});
    }

    protected createPromptEvent(summary: api.content.ContentSummaryAndCompareStatus[]): void {
        new ContentPublishPromptEvent(summary, true).fire();
    }
}

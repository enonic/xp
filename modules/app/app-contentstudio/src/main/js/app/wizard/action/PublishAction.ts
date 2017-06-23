import '../../../api.ts';
import {BasePublishAction} from './BasePublishAction';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {ContentPublishPromptEvent} from '../../browse/ContentPublishPromptEvent';

import i18n = api.util.i18n;

export class PublishAction extends BasePublishAction {
    constructor(wizard: ContentWizardPanel) {
        super({wizard, label: i18n('action.publishMore'), errorMessage: i18n('notify.publish.invalidError')});
    }

    protected createPromptEvent(summary: api.content.ContentSummaryAndCompareStatus[]): void {
        new ContentPublishPromptEvent(summary).fire();
    }
}

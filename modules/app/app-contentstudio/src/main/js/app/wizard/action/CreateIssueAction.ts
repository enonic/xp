import '../../../api.ts';
import {BasePublishAction} from './BasePublishAction';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {CreateIssuePromptEvent} from '../../browse/CreateIssuePromptEvent';

import i18n = api.util.i18n;

export class CreateIssueAction extends BasePublishAction {
    constructor(wizard: ContentWizardPanel) {
        super({wizard, label: i18n('action.createIssueMore'), omitCanPublishCheck: true});
    }

    protected createPromptEvent(summary: api.content.ContentSummaryAndCompareStatus[]): void {
        new CreateIssuePromptEvent(summary).fire();
    }
}

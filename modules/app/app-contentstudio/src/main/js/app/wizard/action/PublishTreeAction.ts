import '../../../api.ts';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {PublishAction} from './PublishAction';
import i18n = api.util.i18n;

export class PublishTreeAction extends PublishAction {

    constructor(wizard: ContentWizardPanel) {
        super(wizard, true);
        this.setLabel(i18n('action.publishTreeMore'));
    }
}

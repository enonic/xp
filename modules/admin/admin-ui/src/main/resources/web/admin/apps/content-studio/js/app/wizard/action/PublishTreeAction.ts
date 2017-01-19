import '../../../api.ts';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {PublishAction} from './PublishAction';

export class PublishTreeAction extends PublishAction {

    constructor(wizard: ContentWizardPanel) {
        super(wizard, true);
        this.setLabel('Publish Tree');
    }
}

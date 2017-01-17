import '../../../api.ts';
import {ShowLiveEditEvent} from '../ShowLiveEditEvent';
import {ContentWizardPanel} from '../ContentWizardPanel';

export class ShowLiveEditAction extends api.ui.Action {

    constructor(wizard: ContentWizardPanel) {
        super('Live');

        this.setEnabled(false);
        this.setTitle('Show Page Editor');
        this.onExecuted(() => {
            wizard.showLiveEdit();
            new ShowLiveEditEvent().fire();
        });
    }
}

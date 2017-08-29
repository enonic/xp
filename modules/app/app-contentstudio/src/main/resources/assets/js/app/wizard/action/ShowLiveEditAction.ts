import '../../../api.ts';
import {ShowLiveEditEvent} from '../ShowLiveEditEvent';
import {ContentWizardPanel} from '../ContentWizardPanel';
import i18n = api.util.i18n;

export class ShowLiveEditAction extends api.ui.Action {

    constructor(wizard: ContentWizardPanel) {
        super('Live');

        this.setEnabled(false);
        this.setTitle(i18n('action.showEditor'));
        this.onExecuted(() => {
            wizard.showLiveEdit();
            new ShowLiveEditEvent().fire();
        });
    }
}

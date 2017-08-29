import '../../../api.ts';
import {ShowSplitEditEvent} from '../ShowSplitEditEvent';
import {ContentWizardPanel} from '../ContentWizardPanel';
import i18n = api.util.i18n;

export class ShowSplitEditAction extends api.ui.Action {

    constructor(wizard: ContentWizardPanel) {
        super(i18n('action.split'));

        this.setEnabled(false);
        this.onExecuted(() => {
            wizard.showSplitEdit();
            new ShowSplitEditEvent().fire();
        });
    }
}

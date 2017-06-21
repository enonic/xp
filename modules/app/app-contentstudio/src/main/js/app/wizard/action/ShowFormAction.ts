import '../../../api.ts';
import {ShowContentFormEvent} from '../ShowContentFormEvent';
import {ContentWizardPanel} from '../ContentWizardPanel';
import i18n = api.util.i18n;

export class ShowFormAction extends api.ui.Action {

    constructor(wizard: ContentWizardPanel) {
        super('Form');

        this.setEnabled(true);
        this.setTitle(i18n('action.hideEditor'));
        this.onExecuted(() => {
            wizard.showForm();
            new ShowContentFormEvent().fire();
        });
    }
}

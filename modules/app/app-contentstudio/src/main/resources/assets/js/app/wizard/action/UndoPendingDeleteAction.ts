import '../../../api.ts';
import {ContentWizardPanel} from '../ContentWizardPanel';

import UndoPendingDeleteContentRequest = api.content.resource.UndoPendingDeleteContentRequest;
import i18n = api.util.i18n;

export class UndoPendingDeleteAction extends api.ui.Action {

    constructor(wizardPanel: ContentWizardPanel) {
        super(i18n('action.undoDelete'));

        this.setEnabled(true);
        this.setVisible(false);

        this.onExecuted(() => {
            new UndoPendingDeleteContentRequest([wizardPanel.getPersistedItem().getContentId()])
                .sendAndParse().then((result: number) => UndoPendingDeleteContentRequest.showResponse(result));
        });
    }
}

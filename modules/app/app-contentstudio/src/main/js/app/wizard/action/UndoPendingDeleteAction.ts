import '../../../api.ts';
import {ContentWizardPanel} from '../ContentWizardPanel';

import UndoPendingDeleteContentRequest = api.content.resource.UndoPendingDeleteContentRequest;

export class UndoPendingDeleteAction extends api.ui.Action {

    constructor(wizardPanel: ContentWizardPanel) {
        super('Undo delete');

        this.setEnabled(true);
        this.setVisible(false);

        this.onExecuted(() => {
            new UndoPendingDeleteContentRequest([wizardPanel.getPersistedItem().getContentId()])
                .sendAndParse().then((result: number) => UndoPendingDeleteContentRequest.showResponse(result));
        });
    }
}

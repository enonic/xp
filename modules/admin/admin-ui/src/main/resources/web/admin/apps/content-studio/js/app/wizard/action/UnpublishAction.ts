import "../../../api.ts";

import {ContentWizardPanel} from "../ContentWizardPanel";
import {ContentPublishPromptEvent} from "../../browse/ContentPublishPromptEvent";

export class UnpublishAction extends api.ui.Action {

    constructor(wizard: ContentWizardPanel) {
        super("Unpublish");

        this.onExecuted(() => {
            console.log('Unpublish action')
        });

    }
}

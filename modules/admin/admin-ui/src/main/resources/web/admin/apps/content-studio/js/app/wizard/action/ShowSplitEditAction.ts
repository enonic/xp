import "../../../api.ts";
import {ShowSplitEditEvent} from "../ShowSplitEditEvent";
import {ContentWizardPanel} from "../ContentWizardPanel";

export class ShowSplitEditAction extends api.ui.Action {

    constructor(wizard: ContentWizardPanel) {
        super('Split');

        this.setEnabled(false);
        this.onExecuted(() => {
            wizard.showSplitEdit();
            new ShowSplitEditEvent().fire();
        });
    }
}

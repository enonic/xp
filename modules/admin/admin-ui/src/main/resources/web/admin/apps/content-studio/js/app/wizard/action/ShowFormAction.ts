import "../../../api.ts";
import {ShowContentFormEvent} from "../ShowContentFormEvent";
import {ContentWizardPanel} from "../ContentWizardPanel";

export class ShowFormAction extends api.ui.Action {

    constructor(wizard: ContentWizardPanel) {
        super("Form");

        this.setEnabled(true);
        this.setTitle("Hide Page Editor");
        this.onExecuted(() => {
            wizard.showForm();
            new ShowContentFormEvent().fire();
        });
    }
}

module app_wizard {

    export class SaveRelationshipTypeAction extends api_ui.Action {

        constructor() {
            super("Save");
        }
    }

    export class CloseRelationshipTypeAction extends api_ui.Action {

        constructor(wizardPanel:api_app_wizard.WizardPanel, checkCanClose?:bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                wizardPanel.close(checkCanClose);
            });
        }
    }
}
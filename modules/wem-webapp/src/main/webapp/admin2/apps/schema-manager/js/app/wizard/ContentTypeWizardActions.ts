module app_wizard {

    export class SaveContentTypeAction extends api_ui.Action {

        constructor() {
            super("Save");
        }
    }

    export class CloseContentTypeAction extends api_ui.Action {

        constructor(wizardPanel:api_app_wizard.WizardPanel, checkCanClose?:bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                wizardPanel.close(checkCanClose);
            });
        }
    }
}
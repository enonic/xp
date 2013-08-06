module app_wizard {

    export class SaveMixinAction extends api_ui.Action {

        constructor() {
            super("Save");
        }
    }

    export class CloseMixinAction extends api_ui.Action {

        constructor(wizardPanel:api_app_wizard.WizardPanel, checkCanClose?:bool = true) {
            super("Close");

            this.addExecutionListener(() => {
                wizardPanel.close(checkCanClose);
            });
        }
    }
}
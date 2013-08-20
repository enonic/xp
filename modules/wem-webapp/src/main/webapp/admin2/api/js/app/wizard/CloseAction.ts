module api_app_wizard {

    export class CloseAction extends api_ui.Action {

        constructor(wizardPanel:api_app_wizard.WizardPanel, checkCanClose?:bool = true) {
            super("Close", "mod+f4");
            this.addExecutionListener(() => {
                wizardPanel.close(checkCanClose);
            });
        }
    }
}
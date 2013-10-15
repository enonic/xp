module api_app_wizard {

    export class SaveAction extends api_ui.Action {

        constructor(wizardPanel:WizardPanel, callback?:() => void) {
            super("Save");

            this.addExecutionListener(() => {
                wizardPanel.saveChanges(callback);
            });
        }
    }
}
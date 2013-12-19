module api_app_wizard {

    export class SaveAction extends api_ui.Action {

        constructor(wizardPanel:WizardPanel<any>) {
            super("Save");

            this.addExecutionListener(() => {
                wizardPanel.saveChanges(() => {

                });
            });
        }
    }
}
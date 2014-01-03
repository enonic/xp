module api.app.wizard {

    export class SaveAction extends api.ui.Action {

        constructor(wizardPanel:WizardPanel<any>) {
            super("Save");

            this.addExecutionListener(() => {
                wizardPanel.saveChanges(() => {

                });
            });
        }
    }
}
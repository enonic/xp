module api.app.wizard {

    export class SaveAction extends api.ui.Action {

        constructor(wizardPanel: WizardPanel<any>) {
            super("Save", "mod+alt+s");

            this.addExecutionListener(() => {
                wizardPanel.saveChanges().
                    done(() => {});
            });
        }
    }
}
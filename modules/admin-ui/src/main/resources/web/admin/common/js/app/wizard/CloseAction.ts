module api.app.wizard {

    export class CloseAction extends api.ui.Action {

        constructor(wizardPanel: api.app.wizard.WizardPanel<any>, checkCanClose: boolean = true) {
            super("Close", "mod+alt+f4", true);
            this.onExecuted(() => {
                wizardPanel.close(checkCanClose);
            });
        }
    }
}
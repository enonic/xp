module api.app.wizard {

    export class CloseAction extends api.ui.Action {

        constructor(wizardPanel: api.app.wizard.WizardPanel<any>, checkCanClose: boolean = true) {
            super("Close", "alt+w", true);
            this.onExecuted(() => {
                wizardPanel.close(checkCanClose);
            });
        }
    }
}
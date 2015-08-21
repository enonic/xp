module api.app.wizard {

    export class CloseAction extends api.ui.Action {

        constructor(wizardPanel: api.app.wizard.WizardPanel<any>, checkCanClose: boolean = true) {
            super("Close", "alt+w", true);
            this.onExecuted(() => {
                debugger;
                if (this.forceExecute) {
                    wizardPanel.close(false);
                } else {
                    wizardPanel.close(checkCanClose);
                }
            });
        }
    }
}
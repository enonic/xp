module api.app.wizard {

    export class SaveAndCloseAction extends api.ui.Action {

        constructor(wizardPanel: WizardPanel<any>) {
            super("SaveAndClose", "mod+enter", true);

            this.onExecuted(() => {

                new SaveAction(wizardPanel).execute();
                new CloseAction(wizardPanel).execute();
            });
        }
    }
}
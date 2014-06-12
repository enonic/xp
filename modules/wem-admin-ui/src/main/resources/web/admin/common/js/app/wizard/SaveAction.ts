module api.app.wizard {

    export class SaveAction extends api.ui.Action {

        constructor(wizardPanel: WizardPanel<any>) {
            super("Save", "mod+s", true);

            this.onExecuted(() => {

                this.setEnabled(false);

                wizardPanel.saveChanges().
                    catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                    finally(() => this.setEnabled(true)).
                    done();
            });
        }
    }
}
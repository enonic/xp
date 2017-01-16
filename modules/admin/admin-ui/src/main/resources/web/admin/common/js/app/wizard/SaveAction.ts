module api.app.wizard {

    export class SaveAction extends api.ui.Action {

        constructor(wizardPanel: WizardPanel<any>, label: string = "Save") {
            super(label, "mod+s", true);

            this.onExecuted(() => {

                this.setEnabled(false);

                return wizardPanel.saveChanges().
                    catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                    finally(() => this.setEnabled(true));
            });
        }
    }
}

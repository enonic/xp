module app.wizard.action {

    export class PublishAction extends api.ui.Action {

        constructor(wizard: app.wizard.ContentWizardPanel) {
            super("Publish");

            this.setEnabled(false);

            this.onExecuted(() => {

                wizard.setPersistAsDraft(false);

                this.setEnabled(false);

                wizard.updatePersistedItem().
                    catch((reason: any) => api.notify.DefaultErrorHandler.handle(reason)).
                    finally(() => this.setEnabled(true)).
                    done();
            });
        }
    }

}

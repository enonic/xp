module app.wizard.action {

    export class PublishAction extends api.ui.Action {

        constructor(wizard: app.wizard.ContentWizardPanel) {
            super("Publish");

            this.setEnabled(false);

            this.onExecuted(() => {

                wizard.setPersistAsDraft(false);

                this.setEnabled(false);

                wizard.updatePersistedItem().
                    catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason)
                    }).
                    finally(() => this.setEnabled(true)).
                    done((content) => {
                        if (content) {
                            new OpenPublishDialogEvent(content).fire();
                        }
                    });
            });
        }
    }

}

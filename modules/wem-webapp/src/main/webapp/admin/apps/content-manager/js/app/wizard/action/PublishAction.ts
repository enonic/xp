module app.wizard.action {

    export class PublishAction extends api.ui.Action {

        constructor(wizard:app.wizard.ContentWizardPanel) {
            super("Publish");
            this.addExecutionListener(() => {
                console.log("Publish action");
                wizard.setPersistAsDraft(false);
                wizard.updatePersistedItem();
            });
        }
    }

}

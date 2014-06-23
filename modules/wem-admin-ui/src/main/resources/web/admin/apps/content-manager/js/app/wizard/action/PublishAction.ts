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
                        console.log("CATCHING!!");
                        api.DefaultErrorHandler.handle(reason)
                    }).
                    finally(() => this.setEnabled(true)).
                    done((content) => {
                        //Using arguments since adding content:api.content.Content gives compiler error. wtf?
                        console.log("updating persisted item", arguments);
                        if (content) {
                            new OpenPublishDialogEvent(content).fire();
                        }
                    });
            });
        }
    }

}

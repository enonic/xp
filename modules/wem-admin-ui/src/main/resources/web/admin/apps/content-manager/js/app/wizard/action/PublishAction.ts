module app.wizard.action {

    import Content = api.content.Content;
    import ContentId = api.content.ContentId;
    import PublishContentRequest = api.content.PublishContentRequest;

    export class PublishAction extends api.ui.Action {

        constructor(wizard: app.wizard.ContentWizardPanel) {
            super("Publish");

            this.setEnabled(false);

            this.onExecuted(() => {

                if (wizard.checkContentCanBePublished()) {
                    wizard.setPersistAsDraft(false);

                    this.setEnabled(false);

                    wizard.updatePersistedItem().
                        then((content) => {
                            if (content) {
                                new PublishContentRequest(new ContentId(content.getId())).send().done(PublishContentRequest.feedback);
                            }
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason)
                        }).finally(() => this.setEnabled(true)).done();
                } else {
                    api.notify.showWarning('The content cannot be published yet. One or more form values are not valid.');
                }
            });
        }
    }

}

module app.wizard.action {

    import Content = api.content.Content;
    import ContentId = api.content.ContentId;

    export class PublishAction extends api.ui.Action {

        constructor(wizard: app.wizard.ContentWizardPanel) {
            super("Publish");

            this.setEnabled(false);

            this.onExecuted(() => {

                if (wizard.checkContentCanBePublished(true)) {
                    wizard.setRequireValid(true);

                    this.setEnabled(false);

                    wizard.saveChanges().
                        then((content) => {
                            if (content) {
                                new app.browse.ContentPublishPromptEvent([content]).fire();
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
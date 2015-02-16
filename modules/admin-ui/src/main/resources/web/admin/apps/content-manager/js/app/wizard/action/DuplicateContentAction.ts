module app.wizard.action {

    export class DuplicateContentAction extends api.ui.Action {

        constructor(wizardPanel: api.app.wizard.WizardPanel<api.content.Content>) {
            super("Duplicate");
            this.onExecuted(() => {
                var source = wizardPanel.getPersistedItem();
                new api.content.DuplicateContentRequest(source.getContentId()).
                    sendAndParse().then((content: api.content.Content) => {
                        // TODO: Replace the returning content with an id
                        api.notify.showFeedback('Content [' + source.getPath() + '] was duplicated!');
                    })
            });
        }
    }
}

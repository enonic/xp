module app.wizard.action {

    export class DeleteContentTypeAction extends api.ui.Action {

        constructor(wizardPanel: api.app.wizard.WizardPanel<api.schema.content.ContentType>) {
            super("Delete", "mod+del");
            this.onExecuted(() => {
                api.ui.dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this content type?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        wizardPanel.close();
                        new api.schema.content.DeleteContentTypeRequest()
                            .addName(wizardPanel.getPersistedItem().getContentTypeName())
                            .send()
                            .done((jsonResponse: api.rest.JsonResponse<api.schema.SchemaDeleteJson>) => {
                                var json = jsonResponse.getResult();

                                if (json.successes && json.successes.length > 0) {
                                    var name = json.successes[0].name;
                                    var deletedContentType = wizardPanel.getPersistedItem();

                                    api.notify.showFeedback('Content [' + name + '] deleted!');
                                    new api.schema.SchemaDeletedEvent([deletedContentType]).fire();
                                }
                            });
                    }).open();
            });
        }
    }
}
module app_wizard_action {

    export class DeleteContentAction extends api_ui.Action {

        constructor(wizardPanel:api_app_wizard.WizardPanel<api_content.Content>) {
            super("Delete", "mod+del");
            this.addExecutionListener(() => {
                api_ui_dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this content?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        wizardPanel.close();
                        new api_content.DeleteContentRequest()
                            .addContentPath(wizardPanel.getPersistedItem().getPath())
                            .send()
                            .done((jsonResponse:api_rest.JsonResponse<any>) => {
                                var json = jsonResponse.getJson();

                                if (json.successes && json.successes.length > 0) {
                                    var path = json.successes[0].path;
                                    var deletedContent = wizardPanel.getPersistedItem();

                                    api_notify.showFeedback('Content [' + path + '] deleted!');
                                    new api_content.ContentDeletedEvent([deletedContent]).fire();
                                }
                            });
                    }).open();
            });
        }
    }

}

module app_wizard {

    export class DuplicateContentAction extends api_ui.Action {

        constructor() {
            super("Duplicate");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

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
                            .done((jsonResponse:api_rest.JsonResponse) => {
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

    export class ShowLiveFormAction extends api_ui.Action {

        constructor() {
            super("LIVE");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowContentLiveEvent().fire();
            });
        }
    }

    export class ShowFormAction extends api_ui.Action {

        constructor() {
            super("FORM");

            this.setEnabled(true);
            this.addExecutionListener(() => {
                new ShowContentFormEvent().fire();
            })
        }
    }

    export class ContentWizardActions implements api_app_wizard.WizardActions<api_content.Content> {

        private save:api_ui.Action;

        private close:api_ui.Action;

        private delete:api_ui.Action;

        private duplicate:api_ui.Action;


        constructor(wizardPanel:api_app_wizard.WizardPanel<api_content.Content>) {
            this.save = new api_app_wizard.SaveAction(wizardPanel);
            this.duplicate = new DuplicateContentAction();
            this.delete = new DeleteContentAction(wizardPanel);
            this.close = new api_app_wizard.CloseAction(wizardPanel);
        }

        enableActionsForNew() {
            this.save.setEnabled( true );
            this.duplicate.setEnabled( false );
            this.delete.setEnabled( false )
        }

        enableActionsForExisting(existing:api_content.Content) {
            this.save.setEnabled( existing.isEditable() );
            this.duplicate.setEnabled( true );
            this.delete.setEnabled( existing.isDeletable() );
        }

        getDeleteAction() {
            return this.delete;
        }

        getSaveAction() {
            return this.save;
        }

        getDuplicateAction() {
            return this.duplicate;
        }

        getCloseAction() {
            return this.close;
        }

    }

}

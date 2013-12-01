module app_wizard {

    export class DuplicateContentTypeAction extends api_ui.Action {

        constructor() {
            super("Duplicate");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DeleteContentTypeAction extends api_ui.Action {

        constructor(wizardPanel:api_app_wizard.WizardPanel<api_schema_content.ContentType>) {
            super("Delete", "mod+del");
            this.addExecutionListener(() => {
                api_ui_dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this content type?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        wizardPanel.close();
                        new api_schema_content.DeleteContentTypeRequest()
                            .addName(wizardPanel.getPersistedItem().getContentTypeName())
                            .send()
                            .done((jsonResponse:api_rest.JsonResponse<api_schema.SchemaDeleteJson>) => {
                                var json = jsonResponse.getResult();

                                if (json.successes && json.successes.length > 0) {
                                    var name = json.successes[0].name;
                                    var deletedContentType = wizardPanel.getPersistedItem();

                                    api_notify.showFeedback('Content [' + name + '] deleted!');
                                    new api_schema.SchemaDeletedEvent([deletedContentType]).fire();
                                }
                            });
                    }).open();
            });
        }
    }

    export class ContentTypeWizardActions implements api_app_wizard.WizardActions<api_schema_content.ContentType> {

        private save:api_ui.Action;

        private close:api_ui.Action;

        private delete:api_ui.Action;

        private duplicate:api_ui.Action;


        constructor(wizardPanel:api_app_wizard.WizardPanel<api_schema_content.ContentType>) {
            this.save = new api_app_wizard.SaveAction(wizardPanel);
            this.duplicate = new DuplicateContentTypeAction();
            this.delete = new DeleteContentTypeAction(wizardPanel);
            this.close = new api_app_wizard.CloseAction(wizardPanel);
        }

        enableActionsForNew() {
            this.save.setEnabled( true );
            this.duplicate.setEnabled( false );
            this.delete.setEnabled( false )
        }

        enableActionsForExisting(existing:api_schema_content.ContentType) {
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

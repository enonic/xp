module app.wizard {

    export class DuplicateSiteTemplateAction extends api.ui.Action {

        constructor() {
            super("Duplicate");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DeleteSiteTemplateAction extends api.ui.Action {

        constructor(wizardPanel:api.app.wizard.WizardPanel<any>) {
            super("Delete", "mod+del");
            /*this.addExecutionListener(() => {
                api.ui.dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this content?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        wizardPanel.close();
                        new api.content.DeleteContentRequest()
                            .addContentPath(wizardPanel.getPersistedItem().getPath())
                            .send()
                            .done((jsonResponse:api.rest.JsonResponse) => {
                                var json = jsonResponse.getJson();

                                if (json.successes && json.successes.length > 0) {
                                    var path = json.successes[0].path;
                                    var deletedContent = wizardPanel.getPersistedItem();

                                    api.notify.showFeedback('Content [' + path + '] deleted!');
                                    new api.content.ContentDeletedEvent([deletedContent]).fire();
                                }
                            });
                    }).open();
            });*/
        }
    }


    export class MoveSiteTemplateAction extends api.ui.Action {

        constructor() {
            super("Move");

            this.setEnabled(true);
        }
    }

    export class SiteTemplateWizardActions implements api.app.wizard.WizardActions<any> {

        private save:api.ui.Action;

        private close:api.ui.Action;

        private delete:api.ui.Action;

        private duplicate:api.ui.Action;

        private move:api.ui.Action;

        constructor(wizardPanel:api.app.wizard.WizardPanel<api.content.Content>) {
            this.save = new api.app.wizard.SaveAction(wizardPanel);
            this.duplicate = new DuplicateSiteTemplateAction();
            this.delete = new DeleteSiteTemplateAction(wizardPanel);
            this.close = new api.app.wizard.CloseAction(wizardPanel);
            this.move = new MoveSiteTemplateAction();
        }

        enableActionsForNew() {
            this.save.setEnabled( true );
            this.duplicate.setEnabled( false );
            this.delete.setEnabled( false )
        }

        enableActionsForExisting(existing:any) {
            /*this.save.setEnabled( existing.isEditable() );
            this.duplicate.setEnabled( true );
            this.delete.setEnabled( existing.isDeletable() );*/
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

        getMoveAction():api.ui.Action {
            return this.move;
        }

    }

}

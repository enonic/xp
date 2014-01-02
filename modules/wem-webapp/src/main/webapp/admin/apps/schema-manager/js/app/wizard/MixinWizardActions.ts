    module app.wizard {

    export class DuplicateMixinAction extends api.ui.Action {

        constructor() {
            super("Duplicate");
            this.addExecutionListener(() => {
                // TODO
            });
        }
    }

    export class DeleteMixinAction extends api.ui.Action {

        constructor(wizardPanel:api.app.wizard.WizardPanel<api.schema.mixin.Mixin>) {
            super("Delete", "mod+del");
            this.addExecutionListener(() => {
                api.ui.dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this mixin?")
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        wizardPanel.close();
                        new api.schema.mixin.DeleteMixinRequest()
                            .addName(wizardPanel.getPersistedItem().getMixinName())
                            .send()
                            .done((jsonResponse:api.rest.JsonResponse<api.schema.SchemaDeleteJson>) => {
                                var json = jsonResponse.getResult();

                                if (json.successes && json.successes.length > 0) {
                                    var name = json.successes[0].name;
                                    var deletedMixin = wizardPanel.getPersistedItem();

                                    api.notify.showFeedback('Content [' + name + '] deleted!');
                                    new api.schema.SchemaDeletedEvent([deletedMixin]).fire();
                                }
                            });
                    }).open();
            });
        }
    }

    export class MixinWizardActions implements api.app.wizard.WizardActions<api.schema.mixin.Mixin> {

        private save:api.ui.Action;

        private close:api.ui.Action;

        private delete:api.ui.Action;

        private duplicate:api.ui.Action;


        constructor(wizardPanel:api.app.wizard.WizardPanel<api.schema.mixin.Mixin>) {
            this.save = new api.app.wizard.SaveAction(wizardPanel);
            this.duplicate = new DuplicateMixinAction();
            this.delete = new DeleteMixinAction(wizardPanel);
            this.close = new api.app.wizard.CloseAction(wizardPanel);
        }

        enableActionsForNew() {
            this.save.setEnabled( true );
            this.duplicate.setEnabled( false );
            this.delete.setEnabled( false )
        }

        enableActionsForExisting(existing:api.schema.mixin.Mixin) {
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

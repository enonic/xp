module app.wizard.action {

    export class ContentWizardActions implements api.app.wizard.WizardActions<api.content.Content> {

        private save:api.ui.Action;

        private close:api.ui.Action;

        private delete:api.ui.Action;

        private duplicate:api.ui.Action;

        private publish:api.ui.Action;

        constructor(wizardPanel:api.app.wizard.WizardPanel<api.content.Content>) {
            this.save = new api.app.wizard.SaveAction(wizardPanel);
            this.duplicate = new DuplicateContentAction();
            this.delete = new DeleteContentAction(wizardPanel);
            this.close = new api.app.wizard.CloseAction(wizardPanel);
            this.publish = new PublishAction();
        }

        enableActionsForNew() {
            this.save.setEnabled( true );
            this.duplicate.setEnabled( false );
            this.delete.setEnabled( false )
        }

        enableActionsForExisting(existing:api.content.Content) {
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

        getPublishAction():api.ui.Action {
            return this.publish;
        }

    }
}

module app_wizard_action {

    export class ContentWizardActions implements api_app_wizard.WizardActions<api_content.Content> {

        private save:api_ui.Action;

        private close:api_ui.Action;

        private delete:api_ui.Action;

        private duplicate:api_ui.Action;

        private publish:api_ui.Action;

        constructor(wizardPanel:api_app_wizard.WizardPanel<api_content.Content>) {
            this.save = new api_app_wizard.SaveAction(wizardPanel);
            this.duplicate = new DuplicateContentAction();
            this.delete = new DeleteContentAction(wizardPanel);
            this.close = new api_app_wizard.CloseAction(wizardPanel);
            this.publish = new PublishAction();
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

        getPublishAction():api_ui.Action {
            return this.publish;
        }

    }
}

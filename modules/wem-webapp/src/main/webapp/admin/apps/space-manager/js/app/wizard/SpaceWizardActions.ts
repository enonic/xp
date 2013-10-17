module app_wizard
{

    export class DuplicateSpaceAction extends api_ui.Action
    {
        constructor()
        {
            super( "Duplicate" );
        }
    }

    export class DeleteSpaceAction extends api_ui.Action
    {
        constructor()
        {
            super( "Delete" );
            this.setEnabled( false );
        }
    }

    export class SpaceWizardActions implements api_app_wizard.WizardActions<api_remote_space.Space>
    {
        private save:api_ui.Action;

        private close:api_ui.Action;

        private delete:api_ui.Action;

        private duplicate:api_ui.Action;


        constructor(wizardPanel:api_app_wizard.WizardPanel<api_remote_space.Space>) {
            this.save = new api_app_wizard.SaveAction(wizardPanel);
            this.duplicate = new DuplicateSpaceAction();
            this.delete = new DeleteSpaceAction();
            this.close = new api_app_wizard.CloseAction(wizardPanel);
        }

        enableActionsForNew() {
            this.save.setEnabled( true );
            this.duplicate.setEnabled( false );
            this.delete.setEnabled( false )
        }

        enableActionsForExisting(existing:api_remote_space.Space) {
            this.save.setEnabled( existing.editable );
            this.duplicate.setEnabled( true );
            this.delete.setEnabled( existing.deletable );
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

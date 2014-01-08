module app.wizard.action {

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
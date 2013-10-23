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

        constructor() {
            super("Delete");
            this.addExecutionListener(() => {
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
            this.delete = new DeleteContentTypeAction();
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

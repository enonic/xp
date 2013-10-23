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

        constructor() {
            super("Delete");
            this.addExecutionListener(() => {
                // TODO: Ask user: Are you sure?
                // TODO: If no: close dialog
                // TODO: Close WizardPanel without asking to save
                // TODO: Send DeleteContentRequest
                // TODO: If request successful: Remove deleted content from grid (fire api_content.DeletedContentEvent)
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
            this.delete = new DeleteContentAction();
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

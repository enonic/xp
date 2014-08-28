module app.wizard.action {

    export class ContentTypeWizardActions implements api.app.wizard.WizardActions<api.schema.content.ContentType> {

        private save: api.ui.Action;

        private close: api.ui.Action;

        private delete: api.ui.Action;

        private duplicate: api.ui.Action;


        constructor(wizardPanel: api.app.wizard.WizardPanel<api.schema.content.ContentType>) {
            this.save = new api.app.wizard.SaveAction(wizardPanel);
            this.duplicate = new DuplicateContentTypeAction();
            this.delete = new DeleteContentTypeAction(wizardPanel);
            this.close = new api.app.wizard.CloseAction(wizardPanel);
        }

        enableActionsForNew() {
            this.save.setEnabled(true);
            this.duplicate.setEnabled(false);
            this.delete.setEnabled(false)
        }

        enableActionsForExisting(existing: api.schema.content.ContentType) {
            this.save.setEnabled(existing.isEditable());
            this.duplicate.setEnabled(true);
            this.delete.setEnabled(existing.isDeletable());
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

module app.wizard.action {

    export class PrincipalWizardActions implements api.app.wizard.WizardActions<api.security.Principal> {

        private save: api.ui.Action;

        private close: api.ui.Action;

        private delete: api.ui.Action;

        private duplicate: api.ui.Action;

        constructor(wizardPanel: app.wizard.PrincipalWizardPanel) {
            this.save = new api.app.wizard.SaveAction(wizardPanel);
            this.duplicate = new DuplicatePrincipalAction(wizardPanel);
            this.delete = new DeletePrincipalAction(wizardPanel);
            this.close = new api.app.wizard.CloseAction(wizardPanel);
        }

        enableActionsForNew() {
            this.save.setEnabled(false);
            this.duplicate.setEnabled(false);
            this.delete.setEnabled(false);
        }

        enableActionsForExisting() {
            this.save.setEnabled(true);
            this.duplicate.setEnabled(true);
            this.delete.setEnabled(true);
        }

        getDeleteAction(): api.ui.Action {
            return this.delete;
        }

        getSaveAction(): api.ui.Action {
            return this.save;
        }

        getDuplicateAction(): api.ui.Action {
            return this.duplicate;
        }

        getCloseAction(): api.ui.Action {
            return this.close;
        }
    }
}

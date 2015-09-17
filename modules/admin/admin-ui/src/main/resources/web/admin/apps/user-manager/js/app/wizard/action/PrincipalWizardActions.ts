module app.wizard.action {

    export class PrincipalWizardActions extends UserItemWizardActions<api.security.Principal> {

        private duplicate: api.ui.Action;

        constructor(wizardPanel: app.wizard.PrincipalWizardPanel) {
            super(wizardPanel);
            this.duplicate = new DuplicatePrincipalAction(wizardPanel);
        }

        enableActionsForNew() {
            super.enableActionsForNew();
            this.duplicate.setEnabled(false);
        }

        enableActionsForExisting() {
            super.enableActionsForExisting();
            this.duplicate.setEnabled(true);
        }

        getDuplicateAction(): api.ui.Action {
            return this.duplicate;
        }

    }
}

module app.wizard.action {

    export class DuplicatePrincipalAction extends api.ui.Action {

        constructor(wizardPanel: api.app.wizard.WizardPanel<api.security.Principal>) {
            super("Duplicate");
            this.onExecuted(() => {
                var source = wizardPanel.getPersistedItem();
                // duplicate user request
            });
        }
    }
}

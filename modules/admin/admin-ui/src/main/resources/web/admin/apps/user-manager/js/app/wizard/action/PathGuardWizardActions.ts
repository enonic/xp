module app.wizard.action {
    export class PathGuardWizardActions extends UserItemWizardActions<api.security.PathGuard> {

        constructor(wizardPanel: app.wizard.UserItemWizardPanel<api.security.PathGuard>) {
            super(wizardPanel);
        }
    }
}

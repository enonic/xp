module app.wizard.action {

    export class UserStoreWizardActions extends UserItemWizardActions<api.security.UserStore> {

        constructor(wizardPanel: app.wizard.UserItemWizardPanel<api.security.UserStore>) {
            super(wizardPanel);
        }
    }
}

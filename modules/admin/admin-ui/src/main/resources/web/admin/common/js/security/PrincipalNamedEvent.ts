module api.security {

    export class PrincipalNamedEvent extends api.event.Event {

        private wizard: api.app.wizard.WizardPanel<Principal>;
        private principal: Principal;

        constructor(wizard: api.app.wizard.WizardPanel<Principal>, principal: Principal) {
            super();
            this.wizard = wizard;
            this.principal = principal;
        }

        public getWizard(): api.app.wizard.WizardPanel<Principal> {
            return this.wizard;
        }

        public getPrincipal(): Principal {
            return this.principal;
        }

        static on(handler: (event: PrincipalNamedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: PrincipalNamedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

    }

}
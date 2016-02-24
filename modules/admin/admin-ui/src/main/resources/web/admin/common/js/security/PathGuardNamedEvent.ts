module api.security {

    export class PathGuardNamedEvent extends api.event.Event {

        private wizard: api.app.wizard.WizardPanel<PathGuard>;
        private pathGuard: PathGuard;

        constructor(wizard: api.app.wizard.WizardPanel<PathGuard>, pathGuard: PathGuard) {
            super();
            this.wizard = wizard;
            this.pathGuard = pathGuard;
        }

        public getWizard(): api.app.wizard.WizardPanel<PathGuard> {
            return this.wizard;
        }

        public getPathGuard(): PathGuard {
            return this.pathGuard;
        }

        static on(handler: (event: PathGuardNamedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: PathGuardNamedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

    }

}
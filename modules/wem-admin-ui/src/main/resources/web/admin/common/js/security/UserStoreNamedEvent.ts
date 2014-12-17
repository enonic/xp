module api.security {

    export class UserStoreNamedEvent extends api.event.Event {

        private wizard: api.app.wizard.WizardPanel<UserStore>;
        private userStore: UserStore;

        constructor(wizard: api.app.wizard.WizardPanel<UserStore>, userStore: UserStore) {
            super();
            this.wizard = wizard;
            this.userStore = userStore;
        }

        public getWizard(): api.app.wizard.WizardPanel<UserStore> {
            return this.wizard;
        }

        public getUserStore(): UserStore {
            return this.userStore;
        }

        static on(handler: (event: UserStoreNamedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: UserStoreNamedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

    }

}
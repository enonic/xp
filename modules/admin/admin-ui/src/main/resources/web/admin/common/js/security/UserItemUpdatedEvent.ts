module api.security {

    export class UserItemUpdatedEvent extends api.event.Event {

        private principal: Principal;
        private userStore: UserStore;
        private pathGuard: PathGuard;

        constructor(principal: Principal, userStore: UserStore, pathGuard: PathGuard) {
            super();
            this.principal = principal;
            this.userStore = userStore;
            this.pathGuard = pathGuard;
        }

        public getPrincipal(): Principal {
            return this.principal;
        }

        public getUserStore(): UserStore {
            return this.userStore;
        }

        public getPathGuard(): PathGuard {
            return this.pathGuard;
        }

        static on(handler: (event: UserItemUpdatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: UserItemUpdatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
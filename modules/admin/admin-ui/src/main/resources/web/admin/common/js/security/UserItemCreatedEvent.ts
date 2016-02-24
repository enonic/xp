module api.security {

    export class UserItemCreatedEvent extends api.event.Event {

        private principal: Principal;
        private userStore: UserStore;
        private pathGuard: PathGuard;
        private parentOfSameType: boolean;

        constructor(principal: Principal, userStore: UserStore, pathGuard: PathGuard, parentOfSameType?: boolean) {
            super();
            this.principal = principal;
            this.userStore = userStore;
            this.pathGuard = pathGuard;
            this.parentOfSameType = parentOfSameType;
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

        public isParentOfSameType(): boolean {
            return this.parentOfSameType;
        }

        static on(handler: (event: UserItemCreatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: UserItemCreatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

}
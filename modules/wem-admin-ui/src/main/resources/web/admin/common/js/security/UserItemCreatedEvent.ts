module api.security {

    export class UserItemCreatedEvent extends api.event.Event {

        private principal: Principal;
        private userStore: UserStore;
        private parentOfSameType: boolean;

        constructor(principal: Principal, userStore: UserStore, parentOfSameType?: boolean) {
            super();
            this.principal = principal;
            this.userStore = userStore;
            this.parentOfSameType = parentOfSameType;
        }

        public getPrincipal(): Principal {
            return this.principal;
        }

        public getUserStore(): UserStore {
            return this.userStore;
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
module api.security  {

    export class UserItemDeletedEvent extends api.event.Event {

        private principals: Principal[];

        constructor(principals: Principal[]) {
            super();
            this.principals = principals;
        }

        public getPrincipals(): Principal[] {
            return this.principals;
        }

        static on(handler: (event: UserItemDeletedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: UserItemDeletedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
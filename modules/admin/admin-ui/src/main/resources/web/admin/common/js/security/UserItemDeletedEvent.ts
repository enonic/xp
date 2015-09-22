module api.security {

    export class UserItemDeletedEvent extends api.event.Event {

        private principals: Principal[];

        private userStores: UserStore[];

        constructor(builder: UserItemDeletedEventBuilder) {
            super();
            this.principals = builder.principals;
            this.userStores = builder.userStores;
        }

        public getPrincipals(): Principal[] {
            return this.principals;
        }

        public getUserStores(): UserStore[] {
            return this.userStores;
        }

        public static create(): UserItemDeletedEventBuilder {
            return new UserItemDeletedEventBuilder();
        }

        static on(handler: (event: UserItemDeletedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: UserItemDeletedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

    export class UserItemDeletedEventBuilder {

        principals: Principal[];

        userStores: UserStore[];

        setPrincipals(principals: Principal[]): UserItemDeletedEventBuilder {
            this.principals = principals;
            return this;
        }

        setUserStores(userStores: UserStore[]): UserItemDeletedEventBuilder {
            this.userStores = userStores;
            return this;
        }

        build(): UserItemDeletedEvent {
            return new UserItemDeletedEvent(this);
        }
    }
}
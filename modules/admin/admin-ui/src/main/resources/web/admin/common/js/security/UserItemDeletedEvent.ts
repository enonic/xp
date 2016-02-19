module api.security {

    export class UserItemDeletedEvent extends api.event.Event {

        private principals: Principal[];

        private userStores: UserStore[];

        private pathGuards: PathGuard[];

        constructor(builder: UserItemDeletedEventBuilder) {
            super();
            this.principals = builder.principals;
            this.userStores = builder.userStores;
            this.pathGuards = builder.pathGuards;
        }

        public getPrincipals(): Principal[] {
            return this.principals;
        }

        public getUserStores(): UserStore[] {
            return this.userStores;
        }

        public getPathGuards(): PathGuard[] {
            return this.pathGuards;
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

        pathGuards: PathGuard[];

        setPrincipals(principals: Principal[]): UserItemDeletedEventBuilder {
            this.principals = principals;
            return this;
        }

        setUserStores(userStores: UserStore[]): UserItemDeletedEventBuilder {
            this.userStores = userStores;
            return this;
        }

        setPathGuards(pathGuards: PathGuard[]): UserItemDeletedEventBuilder {
            this.pathGuards = pathGuards;
            return this;
        }

        build(): UserItemDeletedEvent {
            return new UserItemDeletedEvent(this);
        }
    }
}
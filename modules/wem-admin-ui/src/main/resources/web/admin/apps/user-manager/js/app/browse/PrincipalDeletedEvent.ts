module app.browse {
    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;

    export class PrincipalDeletedEvent extends api.event.Event {

        private principals: PrincipalKey[];

        constructor(principals: PrincipalKey[]) {
            super();
            this.principals = principals;
        }

        getPrincipals(): PrincipalKey[] {
            return this.principals;
        }

        static on(handler: (event: PrincipalDuplicatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: PrincipalDuplicatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

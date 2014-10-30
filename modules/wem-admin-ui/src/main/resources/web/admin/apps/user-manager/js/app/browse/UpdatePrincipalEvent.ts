module app.browse {

    import Principal = api.security.Principal;
    import Event = api.event.Event;

    export class UpdatePrincipalEvent extends Event {
        private principals: Principal[];

        constructor(principals: Principal[]) {
            this.principals = principals;
            super();
        }

        getPrincipals(): Principal[] {
            return this.principals;
        }

        static on(handler: (event: UpdatePrincipalEvent) => void) {
            Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: UpdatePrincipalEvent) => void) {
            Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

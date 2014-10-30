module app.browse {
    import Principal = api.security.Principal;
    export class PrincipalSynchronizedEvent extends api.event.Event {

        private principal: Principal;

        constructor(principal: Principal) {
            super();
            this.principal = principal;

        }

        getPrincipal(): Principal {
            return this.principal;
        }

        static on(handler: (event: PrincipalSynchronizedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: PrincipalSynchronizedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

module app.browse {
    import Principal = api.security.Principal;
    export class PrincipalDuplicatedEvent extends api.event.Event {

        private source: Principal;
        private principal: Principal;
        private nextToSource: boolean;

        constructor(principal: Principal, source: Principal, nextToSource: boolean = true) {
            super();
            this.principal = principal;
            this.source = source;
            this.nextToSource = nextToSource;
        }

        getSource(): Principal {
            return this.source;
        }

        getPrincipal(): Principal {
            return this.principal;
        }

        isNextToSource(): boolean {
            return this.nextToSource;
        }

        static on(handler: (event: PrincipalDuplicatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: PrincipalDuplicatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

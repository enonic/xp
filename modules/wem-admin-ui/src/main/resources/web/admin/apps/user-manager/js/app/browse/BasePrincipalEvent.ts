module app.browse {

    export class BasePrincipalEvent extends api.event.Event {

        private principal: api.security.Principal[];

        constructor(principal: api.security.Principal[]) {
            this.principal = principal;
            super();
        }

        getPrincipals(): api.security.Principal[] {
            return this.principal;
        }
    }
}

module app.browse.filter {

    export class PrincipalBrowseSearchEvent extends api.event.Event {

        private principals: api.security.Principal[];

        constructor(principals: api.security.Principal[]) {
            super();
            this.principals = principals;
        }

        getPrincipals(): api.security.Principal[] {
            return this.principals;
        }

        static on(handler: (event: PrincipalBrowseSearchEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: PrincipalBrowseSearchEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
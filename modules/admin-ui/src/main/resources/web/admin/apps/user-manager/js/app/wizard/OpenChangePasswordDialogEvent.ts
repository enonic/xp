module app.wizard {

    import Principal = api.security.Principal;

    export class OpenChangePasswordDialogEvent extends api.event.Event {

        private principal: Principal;

        constructor(principal: Principal) {
            this.principal = principal;
            super();
        }

        getPrincipal() {
            return this.principal;
        }

        static on(handler: (event: OpenChangePasswordDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: OpenChangePasswordDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}
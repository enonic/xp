module app.browse.filter {

    export class PrincipalBrowseResetEvent extends api.event.Event {

        static on(handler: (event: PrincipalBrowseSearchEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: PrincipalBrowseSearchEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
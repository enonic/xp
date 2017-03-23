module api.app.browse.filter {

    export class BrowseFilterResetEvent extends api.event.Event {

        static on(handler: (event: BrowseFilterResetEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: BrowseFilterResetEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

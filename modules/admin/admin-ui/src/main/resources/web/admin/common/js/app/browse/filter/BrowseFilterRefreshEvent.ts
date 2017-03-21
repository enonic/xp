module api.app.browse.filter {

    export class BrowseFilterRefreshEvent extends api.event.Event {

        static on(handler: (event: BrowseFilterRefreshEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: BrowseFilterRefreshEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

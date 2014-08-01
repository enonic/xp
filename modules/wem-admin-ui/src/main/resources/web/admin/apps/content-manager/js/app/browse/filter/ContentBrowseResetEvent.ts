module app.browse.filter {

    export class ContentBrowseResetEvent extends api.event.Event {

        static on(handler: (event: ContentBrowseResetEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ContentBrowseResetEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }

}
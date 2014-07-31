module app.browse.filter {

    export class ContentBrowseResetEvent extends api.event.Event2 {

        static on(handler: (event: ContentBrowseResetEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ContentBrowseResetEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }

}
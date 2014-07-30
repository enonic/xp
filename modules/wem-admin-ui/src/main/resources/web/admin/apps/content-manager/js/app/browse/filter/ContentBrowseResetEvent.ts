module app.browse.filter {

    export class ContentBrowseResetEvent extends api.event.Event {

        constructor() {
            super('contentBrowseResetEvent');
        }

        static on(handler:(event:ContentBrowseResetEvent) => void) {
            api.event.onEvent('contentBrowseResetEvent', handler);
        }
    }

}
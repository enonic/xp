module app_browse_filter {

    export class ContentBrowseSearchEvent extends api_event.Event {

        resultContentIds;

        constructor(resultContentIds?) {
            super('contentBrowseSearchEvent');
            this.resultContentIds = resultContentIds || [];
        }

        getResultContentIds() {
            return this.resultContentIds;
        }

        static on(handler:(event:ContentBrowseSearchEvent) => void) {
            api_event.onEvent('contentBrowseSearchEvent', handler);
        }
    }

    export class ContentBrowseResetEvent extends api_event.Event {

        constructor() {
            super('contentBrowseResetEvent');
        }

        static on(handler:(event:ContentBrowseResetEvent) => void) {
            api_event.onEvent('contentBrowseResetEvent', handler);
        }
    }

}
module app_browse_filter {

    export class ContentBrowseSearchEvent extends api_event.Event {

        contentList:Object[];

        constructor(contentList?:Object[]) {
            super('contentBrowseSearchEvent');
            this.contentList = contentList || [];
        }

        getContentList() {
            return this.contentList;
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
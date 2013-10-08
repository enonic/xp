module app_browse_filter {

    export class ContentBrowseSearchEvent extends api_event.Event {

        private model:api_content_json.ContentSummaryJson[];

        constructor(model?:api_content_json.ContentSummaryJson[]) {
            super('contentBrowseSearchEvent');
            this.model = model || [];
        }

        getJsonModels():api_content_json.ContentSummaryJson[] {
            return this.model;
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
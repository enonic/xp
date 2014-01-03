module app.browse.filter {

    export class ContentBrowseSearchEvent extends api.event.Event {

        private model:api.content.json.ContentSummaryJson[];

        constructor(model?:api.content.json.ContentSummaryJson[]) {
            super('contentBrowseSearchEvent');
            this.model = model || [];
        }

        getJsonModels():api.content.json.ContentSummaryJson[] {
            return this.model;
        }

        static on(handler:(event:ContentBrowseSearchEvent) => void) {
            api.event.onEvent('contentBrowseSearchEvent', handler);
        }
    }

    export class ContentBrowseResetEvent extends api.event.Event {

        constructor() {
            super('contentBrowseResetEvent');
        }

        static on(handler:(event:ContentBrowseResetEvent) => void) {
            api.event.onEvent('contentBrowseResetEvent', handler);
        }
    }

}
module app.browse.filter {

    export class ContentBrowseSearchEvent extends api.event.Event2 {

        private model:api.content.json.ContentSummaryJson[];

        constructor(model?:api.content.json.ContentSummaryJson[]) {
            super();
            this.model = model || [];
        }

        getJsonModels():api.content.json.ContentSummaryJson[] {
            return this.model;
        }

        static on(handler: (event: ContentBrowseSearchEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ContentBrowseSearchEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}
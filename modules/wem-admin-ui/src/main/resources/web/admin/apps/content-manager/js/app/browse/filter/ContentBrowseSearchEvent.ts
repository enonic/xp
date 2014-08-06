module app.browse.filter {

    export class ContentBrowseSearchEvent extends api.event.Event {

        private model: api.content.json.ContentSummaryJson[];

        constructor(model?: api.content.json.ContentSummaryJson[]) {
            super();
            this.model = model || [];
        }

        getJsonModels(): api.content.json.ContentSummaryJson[] {
            return this.model;
        }

        static on(handler: (event: ContentBrowseSearchEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ContentBrowseSearchEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }
}
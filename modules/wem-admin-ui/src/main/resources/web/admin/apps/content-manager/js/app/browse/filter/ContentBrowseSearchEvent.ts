module app.browse.filter {

    export class ContentBrowseSearchEvent extends api.event.Event {

        private model: api.content.ContentSummary[];

        constructor(model?: api.content.ContentSummary[]) {
            super();
            this.model = model || [];
        }

        getContent(): api.content.ContentSummary[] {
            return this.model;
        }

        static on(handler: (event: ContentBrowseSearchEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentBrowseSearchEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
module app.browse {

    export class SaveSortedContentEvent extends api.event.Event {

        private content: api.content.ContentSummary;

        constructor(content: api.content.ContentSummary) {
            this.content = content;
            super();
        }

        getContent(): api.content.ContentSummary {
            return this.content;
        }

        static on(handler: (event: SaveSortedContentEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: SaveSortedContentEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}

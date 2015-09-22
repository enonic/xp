module app.browse {

    export class OpenSortDialogEvent extends api.event.Event {
        private content: api.content.ContentSummary;

        constructor(content: api.content.ContentSummary) {
            super();
            this.content = content;
        }

        getContent(): api.content.ContentSummary {
            return this.content;
        }

        static on(handler: (event: OpenSortDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: OpenSortDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}
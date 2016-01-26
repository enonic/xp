module app.browse {

    export class OpenMoveDialogEvent extends api.event.Event {
        private content: api.content.ContentSummary[];

        constructor(content: api.content.ContentSummary[]) {
            super();
            this.content = content;
        }

        getContentSummaries(): api.content.ContentSummary[] {
            return this.content;
        }

        static on(handler: (event: OpenMoveDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: OpenMoveDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}
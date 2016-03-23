module api.content.event {

    export class EditContentEvent extends api.event.Event {

        private model: api.content.ContentSummaryAndCompareStatus[];

        constructor(model: api.content.ContentSummaryAndCompareStatus[]) {
            this.model = model;
            super();
        }

        getModels(): api.content.ContentSummaryAndCompareStatus[] {
            return this.model;
        }

        static on(handler: (event: EditContentEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: EditContentEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}

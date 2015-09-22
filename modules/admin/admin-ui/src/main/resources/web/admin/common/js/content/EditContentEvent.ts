module api.content {

    export class EditContentEvent extends api.event.Event {

        private model: api.content.ContentSummary[];

        constructor(model: api.content.ContentSummary[]) {
            this.model = model;
            super();
        }

        getModels(): api.content.ContentSummary[] {
            return this.model;
        }

        static on(handler: (event: EditContentEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: EditContentEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

module app.browse {

    export class BaseContentModelEvent extends api.event.Event {

        private model: api.content.ContentSummary[];

        constructor(model: api.content.ContentSummary[]) {
            this.model = model;
            super();
        }

        getModels(): api.content.ContentSummary[] {
            return this.model;
        }
    }
}

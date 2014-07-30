module app.browse {

    export class BaseContentModelEvent extends api.event.Event {

        private model:api.content.ContentSummary[];

        constructor(name:string, model:api.content.ContentSummary[]) {
            this.model = model;
            super(name);
        }

        getModels():api.content.ContentSummary[] {
            return this.model;
        }
    }
}

module app.browse {

    export class BaseContentModelEvent extends api.event.Event2 {

        private model:api.content.ContentSummary[];

        constructor(model:api.content.ContentSummary[]) {
            this.model = model;
            super();
        }

        getModels():api.content.ContentSummary[] {
            return this.model;
        }
    }
}

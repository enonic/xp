module app.browse {

    export class BaseContentModelEvent extends api.event.Event {

        private model: api.content.ContentSummaryAndCompareStatus[];

        constructor(model: api.content.ContentSummaryAndCompareStatus[]) {
            this.model = model;
            super();
        }

        getModels(): api.content.ContentSummaryAndCompareStatus[] {
            return this.model;
        }
    }
}

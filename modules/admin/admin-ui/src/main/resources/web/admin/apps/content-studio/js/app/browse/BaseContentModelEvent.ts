import '../../api.ts';

export class BaseContentModelEvent extends api.event.Event {

    private model: api.content.ContentSummaryAndCompareStatus[];

    constructor(model: api.content.ContentSummaryAndCompareStatus[]) {
        super();

        this.model = model;
    }

    getModels(): api.content.ContentSummaryAndCompareStatus[] {
        return this.model;
    }
}

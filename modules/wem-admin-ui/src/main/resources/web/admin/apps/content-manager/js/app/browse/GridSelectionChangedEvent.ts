module app.browse {

    export class GridSelectionChangeEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api.event.onEvent('gridChange', handler);
        }
    }
}

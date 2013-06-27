module app_event {

    export class GridSelectionChangeEvent extends BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api_event.onEvent('gridChange', handler);
        }
    }
}

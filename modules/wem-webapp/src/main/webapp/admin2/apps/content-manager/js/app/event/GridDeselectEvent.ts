module app_event {

    export class GridDeselectEvent extends BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('removeFromGrid', model);
        }

        static on(handler:(event:app_event.GridDeselectEvent) => void) {
            api_event.onEvent('removeFromGrid', handler);
        }
    }
}

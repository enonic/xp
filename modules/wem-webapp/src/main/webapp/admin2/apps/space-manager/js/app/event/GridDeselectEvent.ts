module app_event {

    export class GridDeselectEvent extends BaseSpaceModelEvent {
        constructor(model:api_model.SpaceModel[]) {
            super('removeFromGrid', model);
        }

        static on(handler:(event:app_event.GridDeselectEvent) => void) {
            api_event.onEvent('removeFromGrid', handler);
        }
    }
}

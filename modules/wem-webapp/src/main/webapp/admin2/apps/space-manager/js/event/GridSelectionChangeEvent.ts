module app_event {

    export class GridSelectionChangeEvent extends BaseSpaceModelEvent {
        constructor(model:api_model.SpaceModel[]) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api_event.onEvent('gridChange', handler);
        }
    }
}

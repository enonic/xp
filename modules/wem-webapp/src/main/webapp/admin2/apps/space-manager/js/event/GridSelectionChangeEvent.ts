module app_event {

    export class GridSelectionChangeEvent extends BaseSpaceModelEvent {
        constructor(model:app_model.SpaceModel[]) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api_event.onEvent('gridChange', handler);
        }
    }
}

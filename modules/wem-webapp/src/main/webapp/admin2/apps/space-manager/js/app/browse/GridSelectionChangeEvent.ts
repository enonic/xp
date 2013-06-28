module app_browse {

    export class GridSelectionChangeEvent extends app_event.BaseSpaceModelEvent {

        constructor(model:api_model.SpaceModel[]) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api_event.onEvent('gridChange', handler);
        }
    }
}

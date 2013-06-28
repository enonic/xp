module app_browse {

    export class GridDeselectEvent extends app_event.BaseSpaceModelEvent {

        constructor(model:api_model.SpaceModel[]) {
            super('removeFromGrid', model);
        }

        static on(handler:(event:GridDeselectEvent) => void) {
            api_event.onEvent('removeFromGrid', handler);
        }
    }
}

module app_browse {

    export class OpenSpaceEvent extends app_event.BaseSpaceModelEvent {

        constructor(model:api_model.SpaceModel[]) {
            super('openSpace', model);
        }

        static on(handler:(event:OpenSpaceEvent) => void) {
            api_event.onEvent('openSpace', handler);
        }
    }

}

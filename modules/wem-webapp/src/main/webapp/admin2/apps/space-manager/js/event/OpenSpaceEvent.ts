module app_event {

    export class OpenSpaceEvent extends SpaceModelEvent {
        constructor(model:app_model.SpaceModel[]) {
            super('openSpace', model);
        }

        static on(handler:(event:OpenSpaceEvent) => void) {
            API_event.onEvent('openSpace', handler);
        }
    }

}

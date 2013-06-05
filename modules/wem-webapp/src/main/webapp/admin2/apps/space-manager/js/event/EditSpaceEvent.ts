module app_event {
    export class EditSpaceEvent extends SpaceModelEvent {

        constructor(model:app_model.SpaceModel[]) {
            super('editSpaceEvent', model);
        }

        static on(handler:(event:EditSpaceEvent) => void) {
            API_event.onEvent('editSpaceEvent', handler);
        }
    }
}

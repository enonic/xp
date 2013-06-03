module APP.event {
    export class EditSpaceEvent extends SpaceModelEvent {

        constructor(model:APP.model.SpaceModel[]) {
            super('editSpaceEvent', model);
        }

        static on(handler:(event:EditSpaceEvent) => void) {
            API_event.onEvent('editSpaceEvent', handler);
        }
    }
}

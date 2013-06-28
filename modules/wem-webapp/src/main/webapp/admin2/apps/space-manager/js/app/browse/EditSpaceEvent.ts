module app_browse {

    export class EditSpaceEvent extends app_event.BaseSpaceModelEvent {

        constructor(model:api_model.SpaceModel[]) {
            super('editSpaceEvent', model);
        }

        static on(handler:(event:EditSpaceEvent) => void) {
            api_event.onEvent('editSpaceEvent', handler);
        }
    }
}

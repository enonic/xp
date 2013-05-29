module APP.event {
    export class EditSpaceEvent extends API.event.Event {

        constructor() {
            super('editSpaceEvent');
        }

        static on(handler:(event:EditSpaceEvent) => void) {
            API.event.onEvent('editSpaceEvent', handler);
        }
    }
}

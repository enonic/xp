module APP.event {
    export class EditSpaceEvent extends API_event.Event {

        constructor() {
            super('editSpaceEvent');
        }

        static on(handler:(event:EditSpaceEvent) => void) {
            API_event.onEvent('editSpaceEvent', handler);
        }
    }
}

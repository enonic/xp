module APP.event {

    export class SaveSpaceEvent extends API.event.Event {
        constructor() {
            super('saveSpace');
        }

        static on(handler:(event:SaveSpaceEvent) => void) {
            API.event.onEvent('saveSpace', handler);
        }
    }

}

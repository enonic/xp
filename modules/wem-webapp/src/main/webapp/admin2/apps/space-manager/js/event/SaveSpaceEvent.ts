module app_event {

    export class SaveSpaceEvent extends API_event.Event {
        constructor() {
            super('saveSpace');
        }

        static on(handler:(event:SaveSpaceEvent) => void) {
            API_event.onEvent('saveSpace', handler);
        }
    }

}

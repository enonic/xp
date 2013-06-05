module app_event {

    export class NewSpaceEvent extends API_event.Event {
        constructor() {
            super('newSpace');
        }

        static on(handler:(event:NewSpaceEvent) => void) {
            API_event.onEvent('newSpace', handler);
        }
    }

}

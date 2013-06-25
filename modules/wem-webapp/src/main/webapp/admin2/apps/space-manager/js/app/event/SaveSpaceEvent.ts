module app_event {

    export class SaveSpaceEvent extends api_event.Event {
        constructor() {
            super('saveSpace');
        }

        static on(handler:(event:SaveSpaceEvent) => void) {
            api_event.onEvent('saveSpace', handler);
        }
    }

}

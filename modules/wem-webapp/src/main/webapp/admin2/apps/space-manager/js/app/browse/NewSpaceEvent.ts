module app_browse {

    export class NewSpaceEvent extends api_event.Event {

        constructor() {
            super('newSpace');
        }

        static on(handler:(event:NewSpaceEvent) => void) {
            api_event.onEvent('newSpace', handler);
        }
    }

}

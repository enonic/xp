module app_browse {

    export class NewContentEvent extends api_event.Event {
        constructor() {
            super('newContent');
        }

        static on(handler:(event:NewContentEvent) => void) {
            api_event.onEvent('newContent', handler);
        }
    }

}

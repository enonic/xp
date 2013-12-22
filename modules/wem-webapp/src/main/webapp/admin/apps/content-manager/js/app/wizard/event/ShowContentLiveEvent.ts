module app_wizard_event {

    export class ShowContentLiveEvent extends api_event.Event {

        constructor() {
            super('showContentLive');
        }

        static on(handler:(event:ShowContentLiveEvent) => void) {
            api_event.onEvent('showContentLive', handler);
        }

    }
}
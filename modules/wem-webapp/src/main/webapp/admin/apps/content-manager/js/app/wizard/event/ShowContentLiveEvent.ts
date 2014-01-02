module app.wizard.event {

    export class ShowContentLiveEvent extends api.event.Event {

        constructor() {
            super('showContentLive');
        }

        static on(handler:(event:ShowContentLiveEvent) => void) {
            api.event.onEvent('showContentLive', handler);
        }

    }
}
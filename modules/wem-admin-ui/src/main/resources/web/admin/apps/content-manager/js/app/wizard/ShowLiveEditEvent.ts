module app.wizard {

    export class ShowLiveEditEvent extends api.event.Event {
        constructor() {
            super('showLiveEdit');
        }

        static on(handler:(event:ShowLiveEditEvent) => void) {
            api.event.onEvent('showLiveEdit', handler);
        }
    }
}
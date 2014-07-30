module app.wizard {

    export class ShowSplitEditEvent extends api.event.Event {
        constructor() {
            super('showSplitEdit');
        }

        static on(handler:(event:ShowSplitEditEvent) => void) {
            api.event.onEvent('showSplitEdit', handler);
        }
    }
}
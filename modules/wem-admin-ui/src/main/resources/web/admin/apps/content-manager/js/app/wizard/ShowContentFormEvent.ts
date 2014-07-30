module app.wizard {

    export class ShowContentFormEvent extends api.event.Event {
        constructor() {
            super('showContentForm');
        }

        static on(handler:(event:ShowContentFormEvent) => void) {
            api.event.onEvent('showContentForm', handler);
        }
    }
}
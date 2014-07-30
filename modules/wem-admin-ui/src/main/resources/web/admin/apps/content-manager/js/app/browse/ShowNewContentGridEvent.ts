module app.browse {

    export class ShowNewContentGridEvent extends api.event.Event {

        constructor() {
            super('showNewContentGridEvent');
        }

        static on(handler:(event:ShowNewContentGridEvent) => void) {
            api.event.onEvent('showNewContentGridEvent', handler);
        }
    }
}

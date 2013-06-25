module app_event {

    export class DeletedEvent extends api_event.Event {
        constructor() {
            super('deleted');
        }

        static on(handler:(event:DeletedEvent) => void) {
            api_event.onEvent('deleted', handler);
        }
    }

}

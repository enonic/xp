module APP.event {

    export class DeletedEvent extends API_event.Event {
        constructor() {
            super('deleted');
        }

        static on(handler:(event:DeletedEvent) => void) {
            API_event.onEvent('deleted', handler);
        }
    }

}

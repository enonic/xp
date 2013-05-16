module APP.event {

    export class DeletedEvent extends API.event.Event {
        constructor() {
            super('deleted');
        }

        static on(handler:(event:DeletedEvent) => void) {
            API.event.onEvent('deleted', handler);
        }
    }

}

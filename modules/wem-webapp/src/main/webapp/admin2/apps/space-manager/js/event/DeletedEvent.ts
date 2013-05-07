module app.event {

    var DELETED:string = 'deleted';

    export class DeletedEvent extends api.event.Event {
        constructor() {
            super(DELETED);
        }
    }

    export function onDeleted(handler:(event:DeletedEvent) => void) {
        api.event.onEvent(DELETED, handler);
    }

}

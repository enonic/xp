
module APP.event {

    var DELETED:string = 'deleted';

    export class DeletedEvent extends API.event.Event {
        constructor() {
            super(DELETED);
        }
    }

    export function onDeleted(handler:(event:DeletedEvent) => void) {
        API.event.onEvent(DELETED, handler);
    }

}

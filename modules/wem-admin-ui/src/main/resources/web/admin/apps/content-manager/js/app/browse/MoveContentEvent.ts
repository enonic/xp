module app.browse {

    export class MoveContentEvent extends BaseContentModelEvent {

        static on(handler: (event: MoveContentEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: MoveContentEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }

}

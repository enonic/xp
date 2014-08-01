module app.browse {

    export class DuplicateContentEvent extends BaseContentModelEvent {

        static on(handler: (event: DuplicateContentEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: DuplicateContentEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }

}

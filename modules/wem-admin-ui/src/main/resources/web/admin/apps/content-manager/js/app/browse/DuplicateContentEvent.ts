module app.browse {

    export class DuplicateContentEvent extends BaseContentModelEvent {

        static on(handler: (event: DuplicateContentEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: DuplicateContentEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }

}

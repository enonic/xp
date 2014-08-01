module app.browse {

    export class EditContentEvent extends BaseContentModelEvent {

        static on(handler: (event: EditContentEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: EditContentEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }
}

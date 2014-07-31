module app.browse {

    export class EditContentEvent extends BaseContentModelEvent {

        static on(handler: (event: EditContentEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: EditContentEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}

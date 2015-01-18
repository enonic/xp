module app.browse {

    export class EditContentEvent extends BaseContentModelEvent {

        static on(handler: (event: EditContentEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: EditContentEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

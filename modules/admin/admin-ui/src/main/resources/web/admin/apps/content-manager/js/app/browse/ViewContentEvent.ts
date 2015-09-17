module app.browse {

    export class ViewContentEvent extends BaseContentModelEvent {

        static on(handler: (event: ViewContentEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ViewContentEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

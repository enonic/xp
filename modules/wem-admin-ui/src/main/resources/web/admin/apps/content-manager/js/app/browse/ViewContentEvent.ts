module app.browse {

    export class ViewContentEvent extends BaseContentModelEvent {

        static on(handler: (event: ViewContentEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ViewContentEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}

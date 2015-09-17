module app.browse {

    export class SortContentEvent extends BaseContentModelEvent {

        static on(handler: (event: SortContentEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: SortContentEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

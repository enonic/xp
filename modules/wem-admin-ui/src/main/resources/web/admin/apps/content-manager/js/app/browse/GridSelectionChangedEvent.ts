module app.browse {

    export class GridSelectionChangeEvent extends BaseContentModelEvent {

        static on(handler: (event: GridSelectionChangeEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: GridSelectionChangeEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

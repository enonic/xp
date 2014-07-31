module app.browse {

    export class GridSelectionChangeEvent extends BaseContentModelEvent {

        static on(handler: (event: GridSelectionChangeEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: GridSelectionChangeEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}

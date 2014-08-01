module app.browse {

    export class ShowPreviewEvent extends BaseContentModelEvent {

        static on(handler: (event: ShowPreviewEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ShowPreviewEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }
}

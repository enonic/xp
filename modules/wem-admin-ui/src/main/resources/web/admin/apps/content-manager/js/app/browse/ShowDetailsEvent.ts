module app.browse {

    export class ShowDetailsEvent extends BaseContentModelEvent {

        static on(handler: (event: ShowDetailsEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ShowDetailsEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}

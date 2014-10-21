module app.browse {

    export class ShowDetailsEvent extends BaseContentModelEvent {

        static on(handler: (event: ShowDetailsEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ShowDetailsEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

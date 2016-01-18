module app.browse {

    export class ShowNewContentGridEvent extends api.event.Event {

        static on(handler: (event: ShowNewContentGridEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ShowNewContentGridEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

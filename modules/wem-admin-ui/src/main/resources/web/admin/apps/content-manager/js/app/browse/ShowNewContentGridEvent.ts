module app.browse {

    export class ShowNewContentGridEvent extends api.event.Event2 {

        static on(handler: (event: ShowNewContentGridEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ShowNewContentGridEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}

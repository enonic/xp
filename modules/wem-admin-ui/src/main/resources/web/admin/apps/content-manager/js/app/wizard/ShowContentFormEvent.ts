module app.wizard {

    export class ShowContentFormEvent extends api.event.Event {

        static on(handler: (event: ShowContentFormEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ShowContentFormEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }
}
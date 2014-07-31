module app.wizard {

    export class ShowContentFormEvent extends api.event.Event2 {

        static on(handler: (event: ShowContentFormEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ShowContentFormEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}
module app.wizard {

    export class ShowLiveEditEvent extends api.event.Event {

        static on(handler: (event: ShowLiveEditEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ShowLiveEditEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }
}
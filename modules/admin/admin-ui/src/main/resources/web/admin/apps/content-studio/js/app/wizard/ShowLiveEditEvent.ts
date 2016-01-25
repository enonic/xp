module app.wizard {

    export class ShowLiveEditEvent extends api.event.Event {

        static on(handler: (event: ShowLiveEditEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ShowLiveEditEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
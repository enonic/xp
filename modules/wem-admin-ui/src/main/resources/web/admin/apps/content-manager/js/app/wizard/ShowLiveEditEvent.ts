module app.wizard {

    export class ShowLiveEditEvent extends api.event.Event2 {

        static on(handler: (event: ShowLiveEditEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ShowLiveEditEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}
module app.wizard {

    export class ShowSplitEditEvent extends api.event.Event {

        static on(handler: (event: ShowSplitEditEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ShowSplitEditEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
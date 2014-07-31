module app.wizard {

    export class ShowSplitEditEvent extends api.event.Event2 {

        static on(handler: (event: ShowSplitEditEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ShowSplitEditEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}
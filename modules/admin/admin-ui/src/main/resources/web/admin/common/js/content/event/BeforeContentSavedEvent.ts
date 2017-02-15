module api.content.event {

    export class BeforeContentSavedEvent extends api.event.Event {

        static on(handler: (event: BeforeContentSavedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: BeforeContentSavedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

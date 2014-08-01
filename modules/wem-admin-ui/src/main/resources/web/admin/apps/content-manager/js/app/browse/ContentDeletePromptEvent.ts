module app.browse {

    export class ContentDeletePromptEvent extends BaseContentModelEvent {

        static on(handler: (event: ContentDeletePromptEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ContentDeletePromptEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }
}

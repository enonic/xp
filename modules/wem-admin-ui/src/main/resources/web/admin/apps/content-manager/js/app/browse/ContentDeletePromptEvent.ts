module app.browse {

    export class ContentDeletePromptEvent extends BaseContentModelEvent {

        static on(handler: (event: ContentDeletePromptEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentDeletePromptEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

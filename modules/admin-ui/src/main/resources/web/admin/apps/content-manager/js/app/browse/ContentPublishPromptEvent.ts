module app.browse {

    export class ContentPublishPromptEvent extends BaseContentModelEvent {

        static on(handler: (event: ContentPublishPromptEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentPublishPromptEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

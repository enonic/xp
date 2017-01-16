module api.content.event {

    import ContentId = api.content.ContentId;

    export class ContentRequiresSaveEvent extends api.event.Event {

        private contentId: ContentId;

        constructor(contentId: ContentId) {
            super();
            this.contentId = contentId;
        }

        getContentId(): ContentId {
            return this.contentId;
        }

        static on(handler: (event: ContentRequiresSaveEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentRequiresSaveEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

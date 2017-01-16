module api.content.image {

    import Content = api.content.Content;

    export class ImageErrorEvent extends api.event.Event {

        private contentId: ContentId;

        constructor(contentId: ContentId) {
            super();
            this.contentId = contentId;
        }

        getContentId(): ContentId {
            return this.contentId;
        }

        static on(handler: (event: ImageErrorEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ImageErrorEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

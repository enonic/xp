module api.content {
    export class ContentMovedEvent extends api.event.Event {

        private contentId: api.content.ContentId;

        constructor(contentId: api.content.ContentId) {
            super();
            this.contentId = contentId
        }

        public getContentId(): api.content.ContentId {
            return this.contentId;
        }

        static on(handler: (event: ContentMovedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentMovedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
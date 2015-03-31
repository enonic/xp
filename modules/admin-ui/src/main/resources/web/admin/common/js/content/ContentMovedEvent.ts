module api.content {
    export class ContentMovedEvent extends api.event.Event {

        private contentIds: api.content.ContentIds;

        constructor(contentIds: api.content.ContentIds) {
            super();
            this.contentIds = contentIds;
        }

        public getContentIds(): api.content.ContentIds {
            return this.contentIds;
        }

        static on(handler: (event: ContentMovedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentMovedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
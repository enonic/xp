module api.content {

    export interface ContentUpdatedEventJson {
        contentId: string;
    }

    export class ContentUpdatedEvent extends api.event.Event {

        private contentId: api.content.ContentId;

        constructor(contentId: api.content.ContentId) {
            super();
            this.contentId = contentId;
        }

        public getContentId(): api.content.ContentId {
            return this.contentId;
        }

        static on(handler: (event: ContentUpdatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentUpdatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

        static fromJson(json: ContentUpdatedEventJson): ContentUpdatedEvent {
            return new ContentUpdatedEvent(new ContentId(json.contentId));
        }
    }
}
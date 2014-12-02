module api.content {

    export interface ContentChildOrderUpdatedEventJson {
        contentId: string;
    }

    export class ContentChildOrderUpdatedEvent extends api.event.Event {

        private contentId: api.content.ContentId;

        constructor(contentId: api.content.ContentId) {
            super();
            this.contentId = contentId;
        }

        public getContentId(): api.content.ContentId {
            return this.contentId;
        }

        static on(handler: (event: ContentChildOrderUpdatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentChildOrderUpdatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

        static fromJson(json: ContentChildOrderUpdatedEventJson): ContentChildOrderUpdatedEvent {
            return new ContentChildOrderUpdatedEvent(new ContentId(json.contentId));
        }
    }
}
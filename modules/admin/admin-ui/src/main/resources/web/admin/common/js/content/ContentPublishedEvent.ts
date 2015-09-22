module api.content {

    export interface ContentPublishedEventJson {
        contentId: string;
    }

    export class ContentPublishedEvent extends api.event.Event {

        private contentId: api.content.ContentId;

        constructor(contentId: api.content.ContentId) {
            super();
            this.contentId = contentId;
        }

        public getContentId(): api.content.ContentId {
            return this.contentId;
        }

        static on(handler: (event: ContentPublishedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentPublishedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

        static fromJson(json: ContentPublishedEventJson): ContentPublishedEvent {
            return new ContentPublishedEvent(new ContentId(json.contentId));
        }
    }
}
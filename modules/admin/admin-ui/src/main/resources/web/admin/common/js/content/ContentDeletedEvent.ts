module api.content {

    export interface ContentDeletedEventJson {
        contentId: string;
    }

    export class ContentDeletedEvent extends api.event.Event {

        private contentId: api.content.ContentId;

        constructor(contentId: api.content.ContentId) {
            super();
            this.contentId = contentId;
        }

        public getContentId(): api.content.ContentId {
            return this.contentId;
        }

        static on(handler: (event: ContentDeletedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentDeletedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

        static fromJson(json: ContentDeletedEventJson): ContentDeletedEvent {
            return new ContentDeletedEvent(new ContentId(json.contentId));
        }
    }
}
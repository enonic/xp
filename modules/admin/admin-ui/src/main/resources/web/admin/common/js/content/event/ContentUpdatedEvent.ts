module api.content.event {

    export class ContentUpdatedEvent extends api.event.Event {

        private contentSummary: api.content.ContentSummary;

        constructor(contentSummary: api.content.ContentSummary) {
            super();
            this.contentSummary = contentSummary;
        }

        public getContentId(): api.content.ContentId {
            return this.contentSummary.getContentId();
        }

        public getContentSummary(): api.content.ContentSummary {
            return this.contentSummary;
        }

        static on(handler: (event: ContentUpdatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentUpdatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

    }
}

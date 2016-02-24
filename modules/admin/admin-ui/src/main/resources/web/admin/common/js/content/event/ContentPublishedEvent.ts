module api.content.event {

    export class ContentPublishedEvent extends api.event.Event {

        private contentSummary: api.content.ContentSummary;

        private compareStatus: api.content.CompareStatus;

        constructor(contentSummary: api.content.ContentSummary, compareStatus: api.content.CompareStatus) {
            super();
            this.contentSummary = contentSummary;
            this.compareStatus = compareStatus;
        }

        public getContentId(): api.content.ContentId {
            return this.contentSummary.getContentId();
        }

        public getContentSummary(): api.content.ContentSummary {
            return this.contentSummary;
        }

        public getCompareStatus(): api.content.CompareStatus {
            return this.compareStatus;
        }

        static on(handler: (event: ContentPublishedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentPublishedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

    }
}
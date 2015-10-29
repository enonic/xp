module api.content {

    export class ContentPublishedEvent extends api.event.Event {

        private contentId: api.content.ContentId;

        private compareStatus: api.content.CompareStatus;

        constructor(contentId: api.content.ContentId, compareStatus: api.content.CompareStatus) {
            super();
            this.contentId = contentId;
            this.compareStatus = compareStatus;
        }

        public getContentId(): api.content.ContentId {
            return this.contentId;
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
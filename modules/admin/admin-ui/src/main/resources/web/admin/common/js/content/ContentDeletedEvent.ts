module api.content {

    export class ContentDeletedEvent extends api.event.Event {

        private contentDeletedItems: ContentDeletedItem[] = [];

        constructor() {
            super();
        }

        addItem(contentId: ContentId, contentPath: api.content.ContentPath): ContentDeletedEvent {
            this.contentDeletedItems.push(new ContentDeletedItem(contentId, contentPath, false));
            return this;
        }

        addPendingItem(contentId: ContentId, contentPath: api.content.ContentPath): ContentDeletedEvent {
            this.contentDeletedItems.push(new ContentDeletedItem(contentId, contentPath, true));
            return this;
        }

        getDeleteditems(): ContentDeletedItem[] {
            return this.contentDeletedItems;
        }

        isEmpty(): boolean {
            return this.contentDeletedItems.length == 0;
        }

        fireIfNotEmpty() {
            if (!this.isEmpty()) {
                this.fire();
            }
        }

        static on(handler: (event: ContentDeletedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentDeletedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

    export class ContentDeletedItem {

        private contentPath: api.content.ContentPath;

        private pending: boolean;

        private contentId: ContentId

        constructor(contentId: ContentId, contentPath: api.content.ContentPath, pending: boolean = false) {
            this.contentPath = contentPath;
            this.pending = pending;
            this.contentId = contentId;
        }

        public getContentPath(): ContentPath {
            return this.contentPath;
        }

        public getContentId(): ContentId {
            return this.contentId;
        }

        public isPending(): boolean {
            return this.pending;
        }
    }
}
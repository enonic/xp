module api.content.event {

    export class ContentDeletedEvent extends api.event.Event {

        private contentDeletedItems: ContentDeletedItem[] = [];
        private undeletedItems: ContentDeletedItem[] = [];

        constructor() {
            super();
        }

        addItem(contentId: ContentId, contentPath: api.content.ContentPath, branch: string): ContentDeletedEvent {
            this.contentDeletedItems.push(new ContentDeletedItem(contentId, contentPath, branch));
            return this;
        }

        addPendingItem(contentSummary: ContentSummaryAndCompareStatus): ContentDeletedEvent {
            this.contentDeletedItems.push(new ContentPendingDeleteItem(contentSummary, true));
            return this;
        }

        addUndeletedItem(contentSummary: ContentSummaryAndCompareStatus): ContentDeletedEvent {
            this.undeletedItems.push(new ContentPendingDeleteItem(contentSummary));
            return this;
        }

        getDeletedItems(): ContentDeletedItem[] {
            return this.contentDeletedItems;
        }

        getUndeletedItems(): ContentDeletedItem[] {
            return this.undeletedItems;
        }

        isEmpty(): boolean {
            return this.contentDeletedItems.length === 0 && this.undeletedItems.length == 0;
        }

        fire(contextWindow: Window = window) {
            if (!this.isEmpty()) {
                super.fire(contextWindow);
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

        private contentId: ContentId;

        private branch: string;

        constructor(contentId: ContentId, contentPath: api.content.ContentPath, branch: string) {
            this.contentPath = contentPath;
            this.contentId = contentId;
            this.branch = branch;
        }

        public getBranch(): string {
            return this.branch;
        }

        public getContentPath(): ContentPath {
            return this.contentPath;
        }

        public getContentId(): ContentId {
            return this.contentId;
        }

        public isPending(): boolean {
            return false;
        }

        public getCompareStatus(): api.content.CompareStatus {
            throw new Error('Must be overridden by inheritors');
        }
    }

    export class ContentPendingDeleteItem extends ContentDeletedItem {

        private pending: boolean;

        private compareStatus: api.content.CompareStatus;

        constructor(contentSummary: ContentSummaryAndCompareStatus, pending: boolean = false) {
            super(contentSummary.getContentId(), contentSummary.getPath(), 'master');

            this.compareStatus = contentSummary.getCompareStatus();
            this.pending = pending;
        }

        public isPending(): boolean {
            return this.pending;
        }

        public getCompareStatus(): api.content.CompareStatus {
            return this.compareStatus;
        }
    }
}

module api.content.event {

    export class ContentDeletedEvent extends api.event.Event {

        private contentDeletedItems: ContentDeletedItem[] = [];

        constructor() {
            super();
        }

        addItem(contentId: ContentId, contentPath: api.content.ContentPath, branch: string): ContentDeletedEvent {
            this.contentDeletedItems.push(new ContentDeletedItem(contentId, contentPath, branch, false));
            return this;
        }

        addPendingItem(contentId: ContentId, contentPath: api.content.ContentPath): ContentDeletedEvent {
            this.contentDeletedItems.push(new ContentDeletedItem(contentId, contentPath, "master", true));
            return this;
        }

        getDeletedItems(): ContentDeletedItem[] {
            return this.contentDeletedItems;
        }

        isEmpty(): boolean {
            return this.contentDeletedItems.length == 0;
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

        private pending: boolean;

        private contentId: ContentId;

        private branch: string;

        constructor(contentId: ContentId, contentPath: api.content.ContentPath, branch: string, pending: boolean = false) {
            this.contentPath = contentPath;
            this.pending = pending;
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
            return this.pending;
        }
    }
}
module api.content.event {

    export class ContentServerEventsHandler {

        private static instance: ContentServerEventsHandler = new ContentServerEventsHandler();

        private contentBrowsePanelExists: boolean = false;

        private contentCreatedListeners: {(changes: ContentServerChange[]):void}[] = [];

        private contentUpdatedListeners: {(changes: ContentServerChange[]):void}[] = [];

        private contentDeletedListeners: {(changes: ContentServerChange[]):void}[] = [];

        private contentMovedListeners: {(changes: ContentServerChange[]):void}[] = [];

        private contentRenamedListeners: {(changes: ContentServerChange[]):void}[] = [];

        private contentPublishListeners: {(changes: ContentServerChange[]):void}[] = [];

        private contentPendingListeners: {(changes: ContentServerChange[]):void}[] = [];

        private contentDuplicateListeners: {(changes: ContentServerChange[]):void}[] = [];

        private contentSortListeners: {(changes: ContentServerChange[]):void}[] = [];

        constructor() {
            // if(instance)
            // throw new Error("use static getInstance() method instead of creation new object"
        }

        static getInstance(): ContentServerEventsHandler {
            return this.instance;
        }

        setContentBrowsePanelCreated() {
            this.contentBrowsePanelExists = true;
        }

        start() {
            var handler = this.contentServerEventHandler.bind(this);

            BatchContentServerEvent.on(handler);
            this.subscribeOwnListeners();
        }

        private subscribeOwnListeners() {
            this.onContentUpdated((changes: ContentServerChange[]) => {
                this.handleContentUpdated(changes);
            });

            this.onContentDeleted((changes: ContentServerChange[]) => {
                this.handleContentDeleted(changes);
            });

            this.onContentPublished((changes: ContentServerChange[]) => {
                this.handleContentPublished(changes);
            });

            this.onContentPending((changes: ContentServerChange[]) => {
                this.handleContentPending(changes);
            });
        }

        private contentServerEventHandler(event: BatchContentServerEvent) {

            var changes = [];
            event.getEvents().forEach((event) => {
                changes = changes.concat(event.getContentChange());
            });

            switch (event.getType()) {
            case ContentServerChangeType.CREATE:
                this.notifyContentCreated(changes);
                break;
            case ContentServerChangeType.UPDATE:
                this.notifyContentUpdated(changes);
                break;
            case ContentServerChangeType.RENAME:
                this.notifyContentRenamed(changes);
                break;
            case ContentServerChangeType.DELETE:
                this.notifyContentDeleted(changes);
            case ContentServerChangeType.PENDING:
                this.notifyContentPending(changes);
                break;
            case ContentServerChangeType.DUPLICATE:
                this.notifyContentDuplicated(changes);
                break;
            case ContentServerChangeType.PUBLISH:
                this.notifyContentPublished(changes);
                break;
            case ContentServerChangeType.MOVE:
                this.notifyContentMoved(changes);
                break;
            case ContentServerChangeType.SORT:
                this.notifyContentSorted(changes);
                break;
            case ContentServerChangeType.UNKNOWN:
                break;
            default:
                //
            }
        }

        private concatContentPaths(changes: ContentServerChange[]): api.content.ContentPath[] {
            var paths = [];
            changes.forEach((change) => {
                paths = paths.concat(change.getContentPaths());
            });
            return paths;
        }

        private handleContentUpdated(changes: ContentServerChange[]) {
            if (!this.contentBrowsePanelExists) {
                ContentSummaryAndCompareStatusFetcher.fetchByPaths(this.concatContentPaths(changes)).
                    then((data: ContentSummaryAndCompareStatus[]) => {
                        data.forEach((el) => {
                            new api.content.event.ContentUpdatedEvent(el.getContentId()).fire();
                        });
                    });
            }
        }

        private handleContentDeleted(changes: ContentServerChange[]) {
            if (!this.contentBrowsePanelExists) {
                var contentDeletedEvent = new ContentDeletedEvent();
                this.concatContentPaths(changes).forEach((path) => {
                    contentDeletedEvent.addItem(null, path);
                });
                contentDeletedEvent.fire();
            }
        }

        private handleContentPending(changes: ContentServerChange[]) {
            if (!this.contentBrowsePanelExists) {
                var contentDeletedEvent = new ContentDeletedEvent();
                ContentSummaryAndCompareStatusFetcher.fetchByPaths(this.concatContentPaths(changes)).
                    then((data: ContentSummaryAndCompareStatus[]) => {
                        data.filter((el) => {
                            return !!el;
                        }).forEach((el) => {
                            contentDeletedEvent.addPendingItem(el.getContentId(), el.getPath());
                        });
                        contentDeletedEvent.fire();
                    });
            }
        }

        private handleContentPublished(changes: ContentServerChange[]) {
            if (!this.contentBrowsePanelExists) {
                ContentSummaryAndCompareStatusFetcher.fetchByPaths(this.concatContentPaths(changes)).
                    then((data: ContentSummaryAndCompareStatus[]) => {
                        data.forEach((el) => {
                            new ContentPublishedEvent(el.getContentId(), el.getCompareStatus()).fire();
                        });
                    });
            }
        }

        onContentCreated(listener: (changes: ContentServerChange[])=>void) {
            this.contentCreatedListeners.push(listener);
        }

        unContentCreated(listener: (changes: ContentServerChange[])=>void) {
            this.contentCreatedListeners =
                this.contentCreatedListeners.filter((currentListener: (changes: ContentServerChange[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentCreated(changes: ContentServerChange[]) {
            this.contentCreatedListeners.forEach((listener: (changes: ContentServerChange[])=>void) => {
                listener.call(this, changes);
            });
        }

        onContentUpdated(listener: (changes: ContentServerChange[])=>void) {
            this.contentUpdatedListeners.push(listener);
        }

        unContentUpdated(listener: (changes: ContentServerChange[])=>void) {
            this.contentUpdatedListeners =
                this.contentUpdatedListeners.filter((currentListener: (changes: ContentServerChange[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentUpdated(changes: ContentServerChange[]) {
            this.contentUpdatedListeners.forEach((listener: (changes: ContentServerChange[])=>void) => {
                listener.call(this, changes);
            });
        }

        onContentDeleted(listener: (changes: ContentServerChange[])=>void) {
            this.contentDeletedListeners.push(listener);
        }

        unContentDeleted(listener: (changes: ContentServerChange[])=>void) {
            this.contentDeletedListeners =
                this.contentDeletedListeners.filter((currentListener: (changes: ContentServerChange[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentDeleted(changes: ContentServerChange[]) {
            this.contentDeletedListeners.forEach((listener: (changes: ContentServerChange[])=>void) => {
                listener.call(this, changes);
            });
        }

        onContentMoved(listener: (changes: ContentServerChange[])=>void) {
            this.contentMovedListeners.push(listener);
        }

        unContentMoved(listener: (changes: ContentServerChange[])=>void) {
            this.contentMovedListeners = this.contentMovedListeners.filter((currentListener: (changes: ContentServerChange[])=>void) => {
                return currentListener != listener;
            });
        }

        private notifyContentMoved(changes: ContentServerChange[]) {
            this.contentMovedListeners.forEach((listener: (changes: ContentServerChange[])=>void) => {
                listener.call(this, changes);
            });
        }

        onContentRenamed(listener: (changes: ContentServerChange[])=>void) {
            this.contentRenamedListeners.push(listener);
        }

        unContentRenamed(listener: (changes: ContentServerChange[])=>void) {
            this.contentRenamedListeners =
                this.contentRenamedListeners.filter((currentListener: (changes: ContentServerChange[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentRenamed(changes: ContentServerChange[]) {
            this.contentRenamedListeners.forEach((listener: (changes: ContentServerChange[])=>void) => {
                listener.call(this, changes);
            });
        }

        onContentDuplicated(listener: (changes: ContentServerChange[])=>void) {
            this.contentDuplicateListeners.push(listener);
        }

        unContentDuplicated(listener: (changes: ContentServerChange[])=>void) {
            this.contentDuplicateListeners =
                this.contentDuplicateListeners.filter((currentListener: (changes: ContentServerChange[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentDuplicated(changes: ContentServerChange[]) {
            this.contentDuplicateListeners.forEach((listener: (changes: ContentServerChange[])=>void) => {
                listener.call(this, changes);
            });
        }

        onContentPublished(listener: (changes: ContentServerChange[])=>void) {
            this.contentPublishListeners.push(listener);
        }

        unContentPublished(listener: (changes: ContentServerChange[])=>void) {
            this.contentPublishListeners =
                this.contentPublishListeners.filter((currentListener: (changes: ContentServerChange[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentPublished(changes: ContentServerChange[]) {
            this.contentPublishListeners.forEach((listener: (changes: ContentServerChange[])=>void) => {
                listener.call(this, changes);
            });
        }

        onContentPending(listener: (changes: ContentServerChange[])=>void) {
            this.contentPendingListeners.push(listener);
        }

        unContentPending(listener: (changes: ContentServerChange[])=>void) {
            this.contentPendingListeners =
                this.contentPendingListeners.filter((currentListener: (changes: ContentServerChange[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentPending(changes: ContentServerChange[]) {
            this.contentPendingListeners.forEach((listener: (changes: ContentServerChange[])=>void) => {
                listener.call(this, changes);
            });
        }

        onContentSorted(listener: (changes: ContentServerChange[])=>void) {
            this.contentSortListeners.push(listener);
        }

        unContentSorted(listener: (changes: ContentServerChange[])=>void) {
            this.contentSortListeners = this.contentSortListeners.filter((currentListener: (changes: ContentServerChange[])=>void) => {
                return currentListener != listener;
            });
        }

        private notifyContentSorted(changes: ContentServerChange[]) {
            this.contentSortListeners.forEach((listener: (changes: ContentServerChange[])=>void) => {
                listener.call(this, changes);
            });
        }

    }
}
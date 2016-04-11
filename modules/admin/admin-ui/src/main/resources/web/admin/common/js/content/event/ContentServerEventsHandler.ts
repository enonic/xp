module api.content.event {

    import ContentPath = api.content.ContentPath;

    /**
     * Class that listens to server events and fires UI events
     */
    export class ContentServerEventsHandler {

        private static instance: ContentServerEventsHandler = new ContentServerEventsHandler();

        private handler: (event: BatchContentServerEvent) => void;

        private contentCreatedListeners: {(data: ContentSummaryAndCompareStatus[]):void}[] = [];

        private contentUpdatedListeners: {(data: ContentSummaryAndCompareStatus[]):void}[] = [];

        private contentDeletedListeners: {(paths: ContentServerChangeItem[], pending?: boolean):void}[] = [];

        private contentMovedListeners: {(data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[]):void}[] = [];

        private contentRenamedListeners: {(data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[]):void}[] = [];

        private contentPublishListeners: {(data: ContentSummaryAndCompareStatus[]):void}[] = [];

        private contentPendingListeners: {(data: ContentSummaryAndCompareStatus[]):void}[] = [];

        private contentDuplicateListeners: {(data: ContentSummaryAndCompareStatus[]):void}[] = [];

        private contentSortListeners: {(data: ContentSummaryAndCompareStatus[]):void}[] = [];

        private static debug: boolean = false;

        constructor() {
            // if(instance)
            // throw new Error("use static getInstance() method instead of creation new object"
        }

        static getInstance(): ContentServerEventsHandler {
            return this.instance;
        }

        start() {
            if (!this.handler) {
                this.handler = this.contentServerEventHandler.bind(this);
            }
            BatchContentServerEvent.on(this.handler);
        }

        stop() {
            if (this.handler) {
                BatchContentServerEvent.un(this.handler);
                this.handler = null;
            }
        }


        private contentServerEventHandler(event: BatchContentServerEvent) {
            if (ContentServerEventsHandler.debug) {
                console.debug("ContentServerEventsHandler: received server event", event);
            }

            var changes = event.getEvents().map((change) => change.getContentChange());
            // use new paths in case content was renamed or moved
            var useNewPaths = ContentServerChangeType.RENAME === event.getType() ||
                              ContentServerChangeType.MOVE == event.getType();

            if (event.getType() == ContentServerChangeType.DELETE) {
                // content has already been deleted so no need to fetch summaries
                var changeItems: ContentServerChangeItem[] = changes.reduce((total, change: ContentServerChange) => {
                    return total.concat(change.getChangeItems());
                }, []);
                this.handleContentDeleted(changeItems);

            } else {
                ContentSummaryAndCompareStatusFetcher.fetchByPaths(this.extractContentPaths(changes, useNewPaths))
                    .then((summaries) => {
                        if (ContentServerEventsHandler.debug) {
                            console.debug("ContentServerEventsHandler: fetched summaries", summaries);
                        }
                        switch (event.getType()) {
                        case ContentServerChangeType.CREATE:
                            this.handleContentCreated(summaries);
                            break;
                        case ContentServerChangeType.UPDATE:
                            this.handleContentUpdated(summaries);
                            break;
                        case ContentServerChangeType.RENAME:
                            // also supply old paths in case of rename
                            this.handleContentRenamed(summaries, this.extractContentPaths(changes));
                            break;
                        case ContentServerChangeType.DELETE:
                            // has been handled without fetching summaries
                            break;
                        case ContentServerChangeType.PENDING:
                            this.handleContentPending(summaries);
                            break;
                        case ContentServerChangeType.DUPLICATE:
                            this.handleContentDuplicated(summaries);
                            break;
                        case ContentServerChangeType.PUBLISH:
                            this.handleContentPublished(summaries);
                            break;
                        case ContentServerChangeType.MOVE:
                            // also supply old paths in case of move
                            this.handleContentMoved(summaries, this.extractContentPaths(changes));
                            break;
                        case ContentServerChangeType.SORT:
                            this.handleContentSorted(summaries);
                            break;
                        case ContentServerChangeType.UNKNOWN:
                            break;
                        default:
                            //
                        }
                    });
            }
        }

        private extractContentPaths(changes: ContentServerChange[], useNewPaths?: boolean): ContentPath[] {
            return changes.reduce<ContentPath[]>((prev, curr) => {
                return prev.concat(useNewPaths
                    ? curr.getNewContentPaths()
                    : curr.getChangeItems().map((changeItem: ContentServerChangeItem) => changeItem.getContentPath()));
            }, []);
        }


        private handleContentCreated(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug("ContentServerEventsHandler: created", data);
            }
            this.notifyContentCreated(data);
        }

        private handleContentUpdated(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug("ContentServerEventsHandler: updated", data);
            }
            // TODO: refactor update event to contain multiple contents ?
            data.forEach((el) => {
                new api.content.event.ContentUpdatedEvent(el.getContentSummary()).fire()
            });

            this.notifyContentUpdated(data);
        }

        private handleContentRenamed(data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug("ContentServerEventsHandler: renamed", data, oldPaths);
            }
            this.notifyContentRenamed(data, oldPaths);
        }

        private handleContentDeleted(changeItems: ContentServerChangeItem[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug("ContentServerEventsHandler: deleted", changeItems);
            }
            var contentDeletedEvent = new ContentDeletedEvent();
            changeItems.forEach((changeItem) => {
                contentDeletedEvent.addItem(changeItem.getContentId(), changeItem.getContentPath());
            });
            contentDeletedEvent.fire();

            this.notifyContentDeleted(changeItems);
        }

        private handleContentPending(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug("ContentServerEventsHandler: pending", data);
            }
            var contentDeletedEvent = new ContentDeletedEvent();

            data.filter((el) => {
                return !!el;        // not sure if this check is necessary
            }).forEach((el) => {
                contentDeletedEvent.addPendingItem(el.getContentId(), el.getPath());
            });
            contentDeletedEvent.fire();

            this.notifyContentPending(data);
        }

        private handleContentDuplicated(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug("ContentServerEventsHandler: duplicated", data);
            }
            this.notifyContentDuplicated(data);
        }

        private handleContentPublished(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug("ContentServerEventsHandler: published", data);
            }
            // TODO: refactor publish event to contain multiple contents ?
            data.forEach((el) => {
                new ContentPublishedEvent(el.getContentSummary(), el.getCompareStatus()).fire();
            });
            this.notifyContentPublished(data);
        }

        private handleContentMoved(data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug("ContentServerEventsHandler: moved", data, oldPaths);
            }
            this.notifyContentMoved(data, oldPaths);
        }

        private handleContentSorted(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug("ContentServerEventsHandler: sorted", data);
            }
            this.notifyContentSorted(data);
        }


        onContentCreated(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentCreatedListeners.push(listener);
        }

        unContentCreated(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentCreatedListeners =
                this.contentCreatedListeners.filter((currentListener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentCreated(data: ContentSummaryAndCompareStatus[]) {
            this.contentCreatedListeners.forEach((listener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                listener(data);
            });
        }

        onContentUpdated(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentUpdatedListeners.push(listener);
        }

        unContentUpdated(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentUpdatedListeners =
                this.contentUpdatedListeners.filter((currentListener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentUpdated(data: ContentSummaryAndCompareStatus[]) {
            this.contentUpdatedListeners.forEach((listener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                listener(data);
            });
        }

        onContentDeleted(listener: (paths: ContentServerChangeItem[], pending?: boolean)=>void) {
            this.contentDeletedListeners.push(listener);
        }

        unContentDeleted(listener: (paths: ContentServerChangeItem[], pending?: boolean)=>void) {
            this.contentDeletedListeners =
                this.contentDeletedListeners.filter((currentListener: (paths: ContentServerChangeItem[], pending?: boolean)=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentDeleted(paths: ContentServerChangeItem[], pending?: boolean) {
            this.contentDeletedListeners.forEach((listener: (paths: ContentServerChangeItem[], pending?: boolean)=>void) => {
                listener(paths, pending);
            });
        }

        onContentMoved(listener: (data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[])=>void) {
            this.contentMovedListeners.push(listener);
        }

        unContentMoved(listener: (data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[])=>void) {
            this.contentMovedListeners =
                this.contentMovedListeners.filter((currentListener: (data: ContentSummaryAndCompareStatus[],
                                                                     oldPaths: ContentPath[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentMoved(data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[]) {
            this.contentMovedListeners.forEach((listener: (data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[])=>void) => {
                listener(data, oldPaths);
            });
        }

        onContentRenamed(listener: (data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[])=>void) {
            this.contentRenamedListeners.push(listener);
        }

        unContentRenamed(listener: (data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[])=>void) {
            this.contentRenamedListeners =
                this.contentRenamedListeners.filter((currentListener: (data: ContentSummaryAndCompareStatus[],
                                                                       oldPaths: ContentPath[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentRenamed(data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[]) {
            this.contentRenamedListeners.forEach((listener: (data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[])=>void) => {
                listener(data, oldPaths);
            });
        }

        onContentDuplicated(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentDuplicateListeners.push(listener);
        }

        unContentDuplicated(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentDuplicateListeners =
                this.contentDuplicateListeners.filter((currentListener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentDuplicated(data: ContentSummaryAndCompareStatus[]) {
            this.contentDuplicateListeners.forEach((listener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                listener(data);
            });
        }

        onContentPublished(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentPublishListeners.push(listener);
        }

        unContentPublished(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentPublishListeners =
                this.contentPublishListeners.filter((currentListener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentPublished(data: ContentSummaryAndCompareStatus[]) {
            this.contentPublishListeners.forEach((listener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                listener(data);
            });
        }

        onContentPending(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentPendingListeners.push(listener);
        }

        unContentPending(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentPendingListeners =
                this.contentPendingListeners.filter((currentListener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentPending(data: ContentSummaryAndCompareStatus[]) {
            this.contentPendingListeners.forEach((listener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                listener(data);
            });
        }

        onContentSorted(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentSortListeners.push(listener);
        }

        unContentSorted(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentSortListeners =
                this.contentSortListeners.filter((currentListener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                    return currentListener != listener;
                });
        }

        private notifyContentSorted(data: ContentSummaryAndCompareStatus[]) {
            this.contentSortListeners.forEach((listener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                listener(data);
            });
        }

    }
}
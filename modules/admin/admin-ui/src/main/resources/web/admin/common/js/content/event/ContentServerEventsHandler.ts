module api.content.event {

    import ContentPath = api.content.ContentPath;
    import NodeServerChangeType = api.event.NodeServerChangeType;
    import ContentId = api.content.ContentId;

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

        private contentUnpublishListeners: {(data: ContentSummaryAndCompareStatus[]):void}[] = [];

        private contentPendingListeners: {(data: ContentSummaryAndCompareStatus[]):void}[] = [];

        private contentDuplicateListeners: {(data: ContentSummaryAndCompareStatus[]):void}[] = [];

        private contentSortListeners: {(data: ContentSummaryAndCompareStatus[]):void}[] = [];

        private static debug: boolean = false;

        constructor() {
            // if(instance)
            // throw new Error('use static getInstance() method instead of creation new object');
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
                console.debug('ContentServerEventsHandler: received server event', event);
            }

            let changes = event.getEvents().map((change) => change.getNodeChange());

            if (event.getType() === NodeServerChangeType.DELETE && this.hasDraftBranchChanges(changes)) {
                // content has already been deleted so no need to fetch summaries
                let changeItems: ContentServerChangeItem[] = changes.reduce((total, change: ContentServerChange) => {
                    return total.concat(change.getChangeItems());
                }, []);

                let deletedItems = changeItems.filter(d => d.getBranch() === 'draft');
                let unpublishedItems = changeItems.filter(d => deletedItems.every(deleted => !api.ObjectHelper.equals(deleted.contentId,
                    d.contentId)));

                this.handleContentDeleted(deletedItems);
                api.content.resource.ContentSummaryAndCompareStatusFetcher.fetchByPaths(unpublishedItems.map(item => item.getPath()))
                    .then((summaries) => {
                        this.handleContentUnpublished(summaries);
                    });

            } else if (event.getType() === NodeServerChangeType.MOVE) {
                api.content.resource.ContentSummaryAndCompareStatusFetcher.fetchByPaths(this.extractNewContentPaths(changes))
                    .then((summaries) => {
                        this.handleContentMoved(summaries, this.extractContentPaths(changes));
                    });
            } else {
                api.content.resource.ContentSummaryAndCompareStatusFetcher.fetchByIds(this.extractContentIds(changes))
                    .then((summaries) => {
                        if (ContentServerEventsHandler.debug) {
                            console.debug('ContentServerEventsHandler: fetched summaries', summaries);
                        }
                        switch (event.getType()) {
                        case NodeServerChangeType.CREATE:
                            this.handleContentCreated(summaries);
                            break;
                        case NodeServerChangeType.UPDATE:
                            this.handleContentUpdated(summaries);
                            break;
                        case NodeServerChangeType.RENAME:
                            // also supply old paths in case of rename
                            this.handleContentRenamed(summaries, this.extractContentPaths(changes));
                            break;
                        case NodeServerChangeType.DELETE:
                            // delete from draft has been handled without fetching summaries,
                            // deleting from master is unpublish
                            this.handleContentUnpublished(summaries);
                            break;
                        case NodeServerChangeType.PENDING:
                            this.handleContentPending(summaries);
                            break;
                        case NodeServerChangeType.DUPLICATE:
                            this.handleContentDuplicated(summaries);
                            break;
                        case NodeServerChangeType.PUBLISH:
                            this.handleContentPublished(summaries);
                            break;
                        case NodeServerChangeType.SORT:
                            this.handleContentSorted(summaries);
                            break;
                        case NodeServerChangeType.UNKNOWN:
                            break;
                        default:
                            //
                        }
                    });
            }
        }

        private hasDraftBranchChanges(changes: ContentServerChange[]): boolean {
            return changes.some((change: ContentServerChange) => {
                return change.getChangeItems().some(changeItem => {
                    return changeItem.getBranch() === 'draft';
                });
            });
        }

        private extractContentPaths(changes: ContentServerChange[]): ContentPath[] {
            let contentPaths: ContentPath[] = [];

            changes.forEach((change: ContentServerChange) => {
                change.getChangeItems().forEach((changeItem: ContentServerChangeItem) => {
                    contentPaths.push(changeItem.getPath());
                });
            });

            return contentPaths;
        }

        private extractNewContentPaths(changes: ContentServerChange[]): ContentPath[] {
            let contentPaths: ContentPath[] = [];

            changes.forEach((change: ContentServerChange) => {
                contentPaths = contentPaths.concat(change.getNewPaths());
            });

            return contentPaths;
        }

        private extractContentIds(changes: ContentServerChange[]): ContentId[] {
            let contentIds: ContentId[] = [];

            changes.forEach((change: ContentServerChange) => {
                change.getChangeItems().forEach((changeItem: ContentServerChangeItem) => {
                    contentIds.push(changeItem.getContentId());
                });
            });

            return contentIds;
        }

        private handleContentCreated(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug('ContentServerEventsHandler: created', data);
            }
            this.notifyContentCreated(data);
        }

        private handleContentUpdated(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug('ContentServerEventsHandler: updated', data);
            }
            // TODO: refactor update event to contain multiple contents ?
            data.forEach((el) => {
                new api.content.event.ContentUpdatedEvent(el.getContentSummary()).fire();
            });

            this.notifyContentUpdated(data);
        }

        private handleContentRenamed(data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug('ContentServerEventsHandler: renamed', data, oldPaths);
            }
            this.notifyContentRenamed(data, oldPaths);
        }

        private handleContentDeleted(changeItems: ContentServerChangeItem[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug('ContentServerEventsHandler: deleted', changeItems);
            }
            let contentDeletedEvent = new ContentDeletedEvent();
            changeItems.forEach((changeItem) => {
                contentDeletedEvent.addItem(changeItem.getContentId(), changeItem.getPath(), changeItem.getBranch());
            });
            contentDeletedEvent.fire();

            this.notifyContentDeleted(changeItems);
        }

        private handleContentPending(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug('ContentServerEventsHandler: pending', data);
            }
            let contentDeletedEvent = new ContentDeletedEvent();

            data.filter((el) => {
                return !!el;        // not sure if this check is necessary
            }).forEach((el) => {

                if (api.content.CompareStatusChecker.isPendingDelete(el.getCompareStatus())) {
                    contentDeletedEvent.addPendingItem(el);
                } else {
                    contentDeletedEvent.addUndeletedItem(el);
                }
            });
            contentDeletedEvent.fire();

            this.notifyContentPending(data);
        }

        private handleContentDuplicated(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug('ContentServerEventsHandler: duplicated', data);
            }
            this.notifyContentDuplicated(data);
        }

        private handleContentPublished(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug('ContentServerEventsHandler: published', data);
            }

            this.notifyContentPublished(data);
        }

        private handleContentUnpublished(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug('ContentServerEventsHandler: unpublished', data);
            }

            this.notifyContentUnpublished(data);
        }

        private handleContentMoved(data: ContentSummaryAndCompareStatus[], oldPaths: ContentPath[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug('ContentServerEventsHandler: moved', data, oldPaths);
            }
            this.notifyContentMoved(data, oldPaths);
        }

        private handleContentSorted(data: ContentSummaryAndCompareStatus[]) {
            if (ContentServerEventsHandler.debug) {
                console.debug('ContentServerEventsHandler: sorted', data);
            }
            this.notifyContentSorted(data);
        }

        onContentCreated(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentCreatedListeners.push(listener);
        }

        unContentCreated(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentCreatedListeners =
                this.contentCreatedListeners.filter((currentListener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                    return currentListener !== listener;
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
                    return currentListener !== listener;
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
                    return currentListener !== listener;
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
                    return currentListener !== listener;
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
                    return currentListener !== listener;
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
                    return currentListener !== listener;
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
                    return currentListener !== listener;
                });
        }

        private notifyContentPublished(data: ContentSummaryAndCompareStatus[]) {
            this.contentPublishListeners.forEach((listener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                listener(data);
            });
        }

        onContentUnpublished(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentUnpublishListeners.push(listener);
        }

        unContentUnpublished(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentUnpublishListeners =
                this.contentUnpublishListeners.filter((currentListener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                    return currentListener !== listener;
                });
        }

        private notifyContentUnpublished(data: ContentSummaryAndCompareStatus[]) {
            this.contentUnpublishListeners.forEach((listener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                listener(data);
            });
        }

        onContentPending(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentPendingListeners.push(listener);
        }

        unContentPending(listener: (data: ContentSummaryAndCompareStatus[])=>void) {
            this.contentPendingListeners =
                this.contentPendingListeners.filter((currentListener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                    return currentListener !== listener;
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
                    return currentListener !== listener;
                });
        }

        private notifyContentSorted(data: ContentSummaryAndCompareStatus[]) {
            this.contentSortListeners.forEach((listener: (data: ContentSummaryAndCompareStatus[])=>void) => {
                listener(data);
            });
        }

    }
}

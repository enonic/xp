module app.browse.action {

    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import BrowseItemsChanges = api.app.browse.BrowseItemsChanges;
    import ContentBrowseItem = app.browse.ContentBrowseItem;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentId = api.content.ContentId;

    export class PreviewContentHandler {

        private renderableIds: ContentId[] = [];

        private previewStateChangedListeners: {(active: boolean): void}[] = [];

        private blocked: boolean = false;

        public static BLOCK_COUNT: number = 10;

        updateState(contentBrowseItems: ContentBrowseItem[],
                    changes: BrowseItemsChanges<ContentSummaryAndCompareStatus>): wemQ.Promise<any> {

            if (PreviewContentHandler.BLOCK_COUNT < contentBrowseItems.length) {
                this.blocked = true;
                this.notifyPreviewStateChanged(true);
                return;
            } else {
                if (this.blocked) {
                    this.setRenderableIds([]);
                    changes.setAdded(contentBrowseItems);
                }
                this.blocked = false;
            }

            return this.getRenderablePromise(changes);


        }
        private getRenderablePromise(changes: BrowseItemsChanges<ContentSummaryAndCompareStatus>): wemQ.Promise<any> {
            var promises = this.processChanges(changes);

            return wemQ.all(promises).
                spread<void>(() => wemQ<void>(null)).
                then(() => {

                    let removedItems = changes.getRemoved();
                    if (removedItems && removedItems.length > 0) {
                        this.removeRenderableIds(changes.getRemoved().
                            map(item => item.getModel().getContentSummary().getContentId()));

                        if (this.getRenderableIds().length > 0) {
                            this.notifyPreviewStateChanged(true);
                        } else {
                            this.notifyPreviewStateChanged(false);
                        }
                    }

                }).
                catch((reason: any) => api.DefaultErrorHandler.handle(reason));
        }

        private processChanges(changes: BrowseItemsChanges<ContentSummaryAndCompareStatus>): wemQ.Promise<any>[] {

            return changes.getAdded().map((contentBrowseItem) => {

                let contentSummary = contentBrowseItem.getModel().getContentSummary();

                return new api.content.page.IsRenderableRequest(contentSummary.getContentId()).sendAndParse()
                    .then((value: boolean) => {

                        var contentBrowseItem = changes.getAdded().filter(item => item.getId() == contentSummary.getId())[0];
                        contentBrowseItem.setRenderable(value);

                        if (value) {
                            this.notifyPreviewStateChanged(value);
                            this.addRenderableIds([contentSummary.getContentId()]);
                        }

                    }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
            });

        }

        isBlocked(): boolean {
            return this.blocked;
        }

        setRenderableIds(contentIds: ContentId[]) {
            this.renderableIds = contentIds;
        }

        getRenderableIds() {
            return this.renderableIds;
        }

        addRenderableIds(contentIds: ContentId[]) {
            if (contentIds) {
                contentIds.forEach((contentId) => {
                    if (this.renderableIds.indexOf(contentId) == -1) {
                        this.renderableIds.push(contentId);
                    }
                })
            }
        }

        removeRenderableIds(contentIds: ContentId[]) {
            if (contentIds) {
                contentIds.forEach((contentId) => {
                    var index = this.renderableIds.indexOf(contentId);
                    if (index >= 0) {
                        this.renderableIds.splice(index, 1);
                    }
                })
            }
        }


        onPreviewStateChanged(listener: (active: boolean) => void) {
            this.previewStateChangedListeners.push(listener);
        }

        unPreviewStateChanged(listener: (active: boolean) => void) {
            this.previewStateChangedListeners = this.previewStateChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyPreviewStateChanged(active: boolean) {
            this.previewStateChangedListeners.forEach((listener) => {
                listener(active);
            })
        }

    }
}
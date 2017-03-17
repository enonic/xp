import '../../../../api.ts';
import {ContentBrowseItem} from '../../ContentBrowseItem';

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import BrowseItemsChanges = api.app.browse.BrowseItemsChanges;
import BrowseItem = api.app.browse.BrowseItem;
import ContentId = api.content.ContentId;

export class PreviewContentHandler {

    private renderableIds: string[] = [];

    private anyRenderable: boolean;

    private previewStateChangedListeners: {(active: boolean): void}[] = [];

    private blocked: boolean = false;

    public static BLOCK_COUNT: number = 10;

    updateState(contentBrowseItems: ContentBrowseItem[],
                changes?: BrowseItemsChanges<ContentSummaryAndCompareStatus>): wemQ.Promise<any> {

        if (contentBrowseItems.length === 0) {
            if (changes && changes.getRemoved().length > 0) {
                // items have been removed from selection
                this.removeRenderableIds(changes.getRemoved().map(item => item.getModel().getContentId()));
            } else if (!changes) {
                this.renderableIds = [];
            }
            this.notifyPreviewStateChangedIfNeeded();
            return;
        }
        if (PreviewContentHandler.BLOCK_COUNT < contentBrowseItems.length) {
            this.setBlocked(true);
            return;
        }

        if (this.isBlocked() || !changes) {
            this.setRenderableIds([]);
            if (changes) {
                changes.setAdded(contentBrowseItems);
            }
        }
        this.setBlocked(false);

        return this.getRenderablePromise(contentBrowseItems, changes);
    }

    private getRenderablePromise(contentBrowseItems: ContentBrowseItem[],
                                 changes?: BrowseItemsChanges<ContentSummaryAndCompareStatus>): wemQ.Promise<any> {

        return wemQ.all(this.makeRenderableRequests(contentBrowseItems, changes))
            .then((values: boolean[]) => {

                if (changes && changes.getRemoved().length > 0) {
                    // items have been removed from selection
                    this.removeRenderableIds(changes.getRemoved().map(item => item.getModel().getContentId()));
                } else {
                    // fire the changed event if necessary after all requests complete
                    // because remove was done silently
                    this.notifyPreviewStateChangedIfNeeded();
                }

            }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
    }

    public checkIfItemIsRenderable(contentBrowseItem: ContentBrowseItem): wemQ.Promise<any> {
        let contentSummary = contentBrowseItem.getModel().getContentSummary();

        return new api.content.page.IsRenderableRequest(contentSummary.getContentId()).sendAndParse()
            .then((value: boolean) => {
                contentBrowseItem.setRenderable(value);

                return value;
            }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
    }

    private makeRenderableRequests(contentBrowseItems: ContentBrowseItem[],
                                   changes?: BrowseItemsChanges<ContentSummaryAndCompareStatus>): wemQ.Promise<any>[] {

        // check existing items if there are no changes
        // because selected items might have become (not) renderable
        let browseItems = changes && changes.getAdded().length > 0 ? changes.getAdded() : contentBrowseItems;

        return browseItems.map((contentBrowseItem) => {
            let contentSummary = contentBrowseItem.getModel().getContentSummary();

            return new api.content.page.IsRenderableRequest(contentSummary.getContentId()).sendAndParse()
                .then((value: boolean) => {

                    contentBrowseItem.setRenderable(value);

                    if (value) {
                        // item started being renderable or was added to selection
                        // add loudly to enable button as soon as first content is renderable
                        this.addRenderableIds([contentSummary.getContentId()]);
                    } else {
                        // item stopped being renderable
                        // remove silently to avoid button to flickering, but keep renderableIds up to date
                        this.removeRenderableIds([contentSummary.getContentId()], true);
                    }

                    return value;
                }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
        });
    }

    private setBlocked(blocked: boolean) {
        this.blocked = blocked;

        this.notifyPreviewStateChangedIfNeeded();
    }

    isBlocked(): boolean {
        return this.blocked;
    }

    setRenderableIds(contentIds: ContentId[], silent?: boolean) {
        this.renderableIds = contentIds ? contentIds.map(contentId => contentId.toString()) : null;
        if (!silent) {
            this.notifyPreviewStateChangedIfNeeded();
        }
    }

    getRenderableIds() {
        return this.renderableIds;
    }

    private addRenderableIds(contentIds: ContentId[], silent ?: boolean) {
        if (contentIds) {
            contentIds.forEach((contentId) => {
                if (this.renderableIds.indexOf(contentId.toString()) === -1) {
                    this.renderableIds.push(contentId.toString());
                }
            });
            if (!silent) {
                this.notifyPreviewStateChangedIfNeeded();
            }
        }
    }

    private removeRenderableIds(contentIds: ContentId[], silent ?: boolean) {
        let wasRemoved = false;
        if (contentIds) {
            contentIds.forEach((contentId) => {
                let index = this.renderableIds.indexOf(contentId.toString());
                if (index >= 0) {
                    wasRemoved = true;
                    this.renderableIds.splice(index, 1);
                }
            });
            if (!silent || wasRemoved) {
                this.notifyPreviewStateChangedIfNeeded();
            }
        }
    }

    private notifyPreviewStateChangedIfNeeded() {
        let newRenderable = this.isBlocked() || this.renderableIds.length > 0;
        if (newRenderable !== this.anyRenderable) {
            this.notifyPreviewStateChanged(newRenderable);
            this.anyRenderable = newRenderable;
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
        });
    }

}

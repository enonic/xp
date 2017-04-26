import '../../../../../api.ts';
import {WidgetItemView} from '../../WidgetItemView';
import {VersionsView} from './VersionsView';

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class VersionsWidgetItemView extends WidgetItemView {

    private versionsView: VersionsView;

    private gridLoadDeferred: wemQ.Deferred<any>;

    public static debug: boolean = false;

    constructor() {
        super('version-widget-item-view');
        this.managePublishEvent();
    }

    public layout(): wemQ.Promise<any> {
        if (VersionsWidgetItemView.debug) {
            console.debug('VersionsWidgetItemView.layout');
        }
        this.removeChildren();

        return super.layout().then(() => {
            this.versionsView = new VersionsView();
            this.versionsView.onLoaded(() => {
                if (this.gridLoadDeferred) {
                    this.gridLoadDeferred.resolve(null);
                    this.gridLoadDeferred = null;
                }
            });

            this.appendChild(this.versionsView);
        });
    }

    public setContentAndUpdateView(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        if (VersionsWidgetItemView.debug) {
            console.debug('VersionsWidgetItemView.setItem: ', item);
        }

        if (this.versionsView) {
            this.versionsView.setContentData(item);
            return this.reloadActivePanel();
        }
        return wemQ<any>(null);
    }

    private managePublishEvent() {

        let serverEvents = api.content.event.ContentServerEventsHandler.getInstance();

        serverEvents.onContentPublished((contents: ContentSummaryAndCompareStatus[]) => {
            if (this.versionsView && this.versionsView.getContentId()) {
                // check for item because it can be null after publishing pending for delete item
                let itemId = this.versionsView.getContentId();
                let isPublished = contents.some((content) => {
                    return itemId.equals(content.getContentId());
                });

                if (isPublished) {
                    this.reloadActivePanel();
                }
            }
        });
    }

    private reloadActivePanel(): wemQ.Promise<any> {
        if (VersionsWidgetItemView.debug) {
            console.debug('VersionsWidgetItemView.reloadActivePanel');
        }

        if (this.gridLoadDeferred) {
            return this.gridLoadDeferred.promise;
        }

        if (this.versionsView) {
            this.gridLoadDeferred = wemQ.defer<any>();
            this.versionsView.reload()
                .then(() => this.gridLoadDeferred.resolve(null))
                .catch(reason => this.gridLoadDeferred.reject(reason))
                .finally(() => this.gridLoadDeferred = null);

            return this.gridLoadDeferred.promise;
        } else {
            return wemQ(null);
        }
    }

}

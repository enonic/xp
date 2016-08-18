import "../../../../../api.ts";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import {WidgetItemView} from "../../WidgetItemView";
import {VersionsView} from "./VersionsView";

export class VersionsWidgetItemView extends WidgetItemView {

    private versionsView: VersionsView;

    private gridLoadDeferred: wemQ.Deferred<any>;

    public static debug = false;

    constructor() {
        super("version-widget-item-view");
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
            if (this.getItem()) {
                // check for item because it can be null after publishing pending for delete item
                var itemId = this.getItem().getContentId();
                var isPublished = contents.some((content, index, array) => {
                    return itemId.equals(content.getContentId());
                });

                if (isPublished) {
                    this.versionsWidgetItemView.reloadActivePanel();
                }
            }
        });
    }

    public reloadActivePanel(): wemQ.Promise<any> {
        if (VersionsWidgetItemView.debug) {
            console.debug('VersionsWidgetItemView.reloadActivePanel');
        }

        this.gridLoadDeferred = wemQ.defer<any>();
        if (this.versionsView) {
            this.versionsView.reload();
        } else {
            this.gridLoadDeferred.resolve(null);
        }
        return this.gridLoadDeferred.promise;
    }

}

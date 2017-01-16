import "../../../../../api.ts";
import {WidgetItemView} from "../../WidgetItemView";

import CompareStatus = api.content.CompareStatus;
import PublishStatus = api.content.PublishStatus;
import CompareStatusFormatter = api.content.CompareStatusFormatter;
import PublishStatusFormatter = api.content.PublishStatusFormatter;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class StatusWidgetItemView extends WidgetItemView {

    private compareStatus: CompareStatus;
    private publishStatus: PublishStatus;

    public static debug: boolean = false;

    constructor() {
        super('status-widget-item-view');
    }

    public setContentAndUpdateView(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        let compareStatus = item.getCompareStatus();
        let publishStatus = item.getPublishStatus();
        if (StatusWidgetItemView.debug) {
            console.debug('StatusWidgetItemView.setCompareStatus: ', compareStatus);
            console.debug('StatusWidgetItemView.setPublishStatus: ', publishStatus);
        }
        if (compareStatus != this.compareStatus || publishStatus != this.publishStatus) {
            this.compareStatus = compareStatus;
            this.publishStatus = publishStatus;
            return this.layout();
        }
        return wemQ<any>(null);
    }

    public layout(): wemQ.Promise<any> {
        if (StatusWidgetItemView.debug) {
            console.debug('StatusWidgetItemView.layout');
        }

        return super.layout().then(() => {
            if (this.compareStatus != undefined) {
                let statusEl = new api.dom.SpanEl();

                statusEl.addClass(CompareStatus[this.compareStatus].toLowerCase().replace('_', '-') || 'unknown');
                let statusElHtml = CompareStatusFormatter.formatStatus(this.compareStatus).toLocaleUpperCase();

                if (PublishStatus.EXPIRED === this.publishStatus || PublishStatus.PENDING === this.publishStatus) {
                    statusEl.addClass(PublishStatus[this.publishStatus].toLowerCase().replace('_', '-') || 'unknown');
                    statusElHtml += ' (' + PublishStatusFormatter.formatStatus(this.publishStatus).toLocaleUpperCase() + ')';
                }

                statusEl.setHtml(statusElHtml);
                this.removeChildren();
                this.appendChild(statusEl);
            } else {
                this.removeChildren();
            }
        });
    }
}
